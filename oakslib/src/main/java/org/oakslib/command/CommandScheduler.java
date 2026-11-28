package org.oakslib.command;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods", "PMD.TooManyFields"})
public final class CommandScheduler {

    /**
     * The single instance of CommandScheduler. The OpMode should call the
     * run() method periodically.
     */
    private static CommandScheduler instance;

    public static synchronized CommandScheduler getInstance() {
        if (instance == null) {
            instance = new CommandScheduler();
        }
        return instance;
    }

    // A set of currently-running commands.
    private final Set<Command> m_scheduledCommands = new LinkedHashSet<>();

    // Commands scheduled while the run loop is executing, to be started afterwards.
    private final Set<Command> m_toSchedule = new LinkedHashSet<>();

    // Commands canceled while the run loop is executing, to be canceled afterwards.
    private final Set<Command> m_toCancel = new LinkedHashSet<>();

    // A map of subsystems and the commands that currently require them.
    private final Map<Subsystem, Command> m_requirements = new LinkedHashMap<>();

    // A map of all registered subsystems and their default commands.
    private final Map<Subsystem, Command> m_subsystems = new LinkedHashMap<>();

    // A set of all Triggers to be polled.
    private final Set<Runnable> m_triggers = new LinkedHashSet<>();

    // Whether or not the scheduler is currently disabled.
    private boolean m_disabled;

    // Whether or not the scheduler is currently inside run().
    private boolean m_inRunLoop;

    /** Enables the CommandScheduler. */
    public void enable() {
        m_disabled = false;
    }

    /** Disables the CommandScheduler. */
    public void disable() {
        m_disabled = true;
    }

    private void initCommand(Command command, Set<Subsystem> requirements) {
        m_scheduledCommands.add(command);
        for (Subsystem requirement : requirements) {
            m_requirements.put(requirement, command);
        }
        command.initialize();
    }

    private void doCancel(Command command) {
        if (command == null || !m_scheduledCommands.contains(command)) {
            return;
        }
        m_scheduledCommands.remove(command);
        for (Subsystem requirement : command.getRequirements()) {
            m_requirements.remove(requirement);
        }
        command.end(true);
    }
    private void scheduleCommand(Command command) {
        if (command == null || m_scheduledCommands.contains(command)) {
            return;
        }

        for (Subsystem requirement : command.getRequirements()) {
            Command requiring = m_requirements.get(requirement);
            if (requiring != null) {
                doCancel(requiring);
            }
        }

        initCommand(command, command.getRequirements());
    }

    /**
     * Runs a single iteration of the scheduler. The execution occurs in the following order:
     *
     * <p>Subsystem periodic methods are called.
     *
     * <p>Button bindings are polled, and new commands are scheduled from them.
     *
     * <p>Currently-scheduled commands are executed.
     *
     * <p>End conditions are checked on currently-scheduled commands, and commands that are
     * finished have their end methods called and are removed.
     *
     * <p>Any subsystems not being used as requirements have their default commands started.
     */
    public void run() {
        if (m_disabled) {
            return;
        }

        m_inRunLoop = true;

        // Run the periodic method of all registered subsystems.
        for (Subsystem subsystem : m_subsystems.keySet()) {
            subsystem.periodic();
        }

        // Poll button bindings.
        for (Runnable trigger : m_triggers) {
            trigger.run();
        }

        // Start commands that were scheduled (e.g. by triggers) during this run loop.
        for (Command command : m_toSchedule) {
            scheduleCommand(command);
        }
        m_toSchedule.clear();

        // Execute currently-scheduled commands.
        for (Command command : m_scheduledCommands) {
            command.execute();
        }

        // Check end conditions, run end methods, and release requirements.
        for (Iterator<Command> iterator = m_scheduledCommands.iterator(); iterator.hasNext(); ) {
            Command command = iterator.next();
            if (command.isFinished()) {
                command.end(false);
                for (Subsystem requirement : command.getRequirements()) {
                    m_requirements.remove(requirement);
                }
                iterator.remove();
            }
        }

        // Cancel commands that were canceled during this run loop.
        for (Command command : m_toCancel) {
            doCancel(command);
        }
        m_toCancel.clear();

        // Start default commands for any subsystem not currently required by a running command.
        for (Map.Entry<Subsystem, Command> entry : m_subsystems.entrySet()) {
            Subsystem subsystem = entry.getKey();
            Command defaultCommand = entry.getValue();
            if (defaultCommand == null || m_scheduledCommands.contains(defaultCommand)) {
                continue;
            }
            boolean inUse = false;
            for (Subsystem requirement : defaultCommand.getRequirements()) {
                Command requiring = m_requirements.get(requirement);
                if (requiring != null && requiring != defaultCommand) {
                    inUse = true;
                    break;
                }
            }
            if (!inUse) {
                initCommand(defaultCommand, defaultCommand.getRequirements());
            }
        }

        m_inRunLoop = false;
    }

    /**
     * Returns the command currently requiring the given subsystem, or null if no command is
     * currently scheduled that requires it.
     */
    public Command requiring(Subsystem subsystem) {
        return m_requirements.get(subsystem);
    }

    /**
     * Schedules a command. Commands scheduled while the run loop is executing (e.g. from trigger
     * bindings) are queued and started when the current run loop iteration finishes.
     */
    public void schedule(Command command) {
        if (command == null) {
            return;
        }
        if (m_inRunLoop) {
            m_toSchedule.add(command);
            return;
        }
        scheduleCommand(command);
    }

    public void schedule(Command... commands) {
        for (Command command : commands) {
            schedule(command);
        }
    }

    /**
     * Cancels a command. Its {@link Command#end(boolean)} method is called with interrupted =
     * true, and its requirements are released so other commands can use them.
     */
    public void cancel(Command command) {
        if (command == null) {
            return;
        }
        if (m_inRunLoop) {
            if (m_scheduledCommands.contains(command)) {
                m_toCancel.add(command);
            }
            return;
        }
        doCancel(command);
    }

    public boolean isScheduled(Command command) {
        return m_scheduledCommands.contains(command);
    }

    /** Cancels every currently-scheduled command. */
    public void cancelAll() {
        for (Command command : new LinkedHashSet<>(m_scheduledCommands)) {
            doCancel(command);
        }
    }

    /**
     * Un-registers subsystems with the scheduler. The subsystem will no longer have its periodic
     * block called, and any command currently requiring it is interrupted.
     *
     * @param subsystems the subsystems to un-register
     */
    public void unregisterSubsystem(Subsystem... subsystems) {
        for (Subsystem subsystem : subsystems) {
            m_subsystems.remove(subsystem);
            Command requiring = m_requirements.remove(subsystem);
            if (requiring != null) {
                doCancel(requiring);
            }
        }
    }

    /**
     * Un-registers all registered Subsystems with the scheduler.
     */
    public void unregisterAllSubsystems() {
        unregisterSubsystem(m_subsystems.keySet().toArray(new Subsystem[0]));
    }

    public void setDefaultCommand(Subsystem subsystem, Command defaultCommand) {
        if (subsystem == null) {
            return;
        }
        if (defaultCommand == null) {
            return;
        }

        if (!defaultCommand.getRequirements().contains(subsystem)) {
            throw new IllegalArgumentException("Default commands must require their subsystem!");
        }

        m_subsystems.put(subsystem, defaultCommand);
    }

    /**
     * Removes the default command for a subsystem. The current default command will run until
     * another command is scheduled that requires the subsystem, at which point the current default
     * command will not be re-scheduled.
     *
     * @param subsystem the subsystem whose default command will be removed
     */
    public void removeDefaultCommand(Subsystem subsystem) {
        if (subsystem == null) {
            return;
        }

        m_subsystems.put(subsystem, null);
    }

    /**
     * Gets the default command associated with this subsystem. Null if this subsystem has no
     * default command associated with it.
     *
     * @param subsystem the subsystem to inquire about
     * @return the default command associated with the subsystem
     */
    public Command getDefaultCommand(Subsystem subsystem) {
        return m_subsystems.get(subsystem);
    }

    public void registerSubsystem(Subsystem... subsystems) {
        for (Subsystem subsystem : subsystems) {
            if (!m_subsystems.containsKey(subsystem)) {
                m_subsystems.put(subsystem, null);
            }
        }
    }

    public void bind(Runnable trigger) {
        m_triggers.add(trigger);
    }

    /**
     * Clears all scheduler state. Call between OpModes so commands, triggers, and default commands
     * from a previous OpMode do not leak into the next one.
     */
    public void reset() {
        cancelAll();
        m_subsystems.clear();
        m_triggers.clear();
        m_toSchedule.clear();
        m_toCancel.clear();
        m_disabled = false;
    }
}