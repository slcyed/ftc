package org.firstinspires.ftc.teamcode.oakslib.command;

import com.qualcomm.ftccommon.FtcEventLoopBase;
import com.qualcomm.robotcore.eventloop.EventLoop;

import org.firstinspires.ftc.robotcore.internal.system.Watchdog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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

    // A list of all Buttons to be Polled

//    private final EventLoop m_activeButtonLoop = ;

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

        //

    }
}
