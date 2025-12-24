package org.firstinspires.ftc.teamcode.oakslib.command;

import org.firstinspires.ftc.robotcore.internal.system.Watchdog;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods", "PMD.TooManyFields"})
public final class CommandScheduler {

    /**
     * The Single Instance of CommandScheduler. OpMode should
     * call the run() method periodically.
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

    // A map of subsystems and the commands that require them.

    private final Map<Subsystem, Command> m_requirements = new LinkedHashMap<>();

    // A map of all subsystems and their default commands.

    private final Map<Subsystem, Command> m_subsystems = new LinkedHashMap<>();

    // Disabled

    private boolean m_disabled;

    // A set of all Triggers to be Polled

    private final Set<Runnable> m_triggers = new LinkedHashSet<>();

    private boolean m_inRunLoop;
    private final Set<Command> m_toSchedule = new LinkedHashSet<>();
    /**
     * Watchdog
     * Period and timeout should be equal to the time between each loop in OpMode. (By default 1ms")
     */

    private final Watchdog m_watchdog = new Watchdog(() -> {}, 1, 1, TimeUnit.MILLISECONDS);

    // Enable the CommandScheduler

    public void enable() {
        m_disabled = false;
    }

    // Disable the CommandScheduler

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



    /**
     * Runs a single iteration of the scheduler. The execution occurs in the following order:
     *
     * <p>Subsystem periodic methods are called.
     *
     * <p>Button bindings are polled, and new commands are scheduled from them.
     *
     * <p>Currently-scheduled commands are executed.
     *
     * <p>End conditions are checked on currently-scheduled commands, and commands that are finished
     * have their end methods called and are removed.
     *
     * <p>Any subsystems not being used as requirements have their default methods started.
     */
    public void run() {
        if (m_disabled) {
            return;
        }

        m_watchdog.stroke();

        // Run the periodic method of all registered subsystems.
        for (Subsystem subsystem : m_subsystems.keySet()) {
            subsystem.periodic();
        }

        // Poll Button Bindings

        for (Runnable trigger : m_triggers) {
            trigger.run();
        }

        // Currently-Scheduled commands are executed

        for (Command command : m_scheduledCommands) {
            command.execute();
        }

        // Check end conditions and run end methods

        for (Command command : m_scheduledCommands) {
            if (command.isFinished()) {
                command.end(false);
            }
        }

        // Run subsystem default methods

        for (Subsystem subsystem : m_subsystems.keySet()) {
            Objects.requireNonNull(m_subsystems.get(subsystem)).execute();
        }

    }

    public Command requiring(Subsystem subsystem) {
        return m_requirements.get(subsystem);
    }

    public void schedule(Command command) {
        if (command == null) {
            return;
        }
        if (m_inRunLoop) {
            m_toSchedule.add(command);
            return;
        }

        Set<Subsystem> requirements = command.getRequirements();

        if (Collections.disjoint(m_requirements.keySet(), requirements)) {
            initCommand(command, requirements);
        } else {
            // Else check if the requirements that are in use have all have interruptible commands,
            // and if so, interrupt those commands and schedule the new command.
            for (Subsystem requirement : requirements) {
                Command requiring = requiring(requirement);
                if (requiring != null) {
                    return;
                }
            }
            for (Subsystem requirement : requirements) {
                Command requiring = requiring(requirement);
                if (requiring != null) {
                    cancel(requiring);
                }
            }
            initCommand(command, requirements);
        }
    }

    public void schedule(Command... commands) {
        for (Command command : commands) {
            schedule(command);
        }
    }

    /**
     * Un-registers subsystems with the scheduler. The subsystem will no longer have its periodic
     * block called, and will not have its default command scheduled.
     *
     * @param subsystems the subsystem to un-register
     */
    public void unregisterSubsystem(Subsystem... subsystems) {
        m_subsystems.keySet().removeAll(Set.of(subsystems));
    }

    /**
     * Un-registers all registered Subsystems with the scheduler. All currently registered subsystems
     * will no longer have their periodic block called, and will not have their default command
     * scheduled.
     */
    public void unregisterAllSubsystems() {
        m_subsystems.clear();
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
     * Removes the default command for a subsystem. The current default command will run until another
     * command is scheduled that requires the subsystem, at which point the current default command
     * will not be re-scheduled.
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
     * Gets the default command associated with this subsystem. Null if this subsystem has no default
     * command associated with it.
     *
     * @param subsystem the subsystem to inquire about
     * @return the default command associated with the subsystem
     */
    public Command getDefaultCommand(Subsystem subsystem) {
        return m_subsystems.get(subsystem);
    }

    public void registerSubsystem(Subsystem... subsystems) {
        for (Subsystem subsystem : subsystems) {
            if (m_subsystems.containsKey(subsystem)) {
                continue;
            }
            m_subsystems.put(subsystem, null);
        }
    }

    public boolean isScheduled(Command command) {
        return m_scheduledCommands.contains(command);
    }

    public void cancel(Command command) {

    }

    public void bind(Runnable trigger) {
        m_triggers.add(trigger);
    }
}
