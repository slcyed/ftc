package org.firstinspires.ftc.teamcode.teleop;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommandScheduler {
    private static CommandScheduler m_CommandScheduler;

    // Registered Subsystem + DefaultCommand
    private static Map<Subsystem,Command> m_subsystems = new LinkedHashMap<>();
    private static Map<Command,Subsystem> m_commands = new LinkedHashMap<>();

    public static void schedule(Command command) {
        m_commands.put(command, (Subsystem) command.getRequirements());
    }

    public static CommandScheduler getInstance() {
        if (m_CommandScheduler == null) {
            m_CommandScheduler = new CommandScheduler();
        }
        return m_CommandScheduler;
    }

    public void registerSubsystem(Subsystem subsystem) {
        m_subsystems.put(subsystem,subsystem.getDefaultCommand());
    }

    public void run() {
        for (Subsystem subsystem : m_subsystems.keySet()) {
            subsystem.periodic();
        }

        for (Command command : m_commands.keySet()) {
            command.initialize();
            while(!command.isFinished()) {
                command.execute();
            }
            command.end();
        }
    }

    public void setDefaultCommand(Subsystem subsystem, Command command) {
        m_subsystems.replace(subsystem,command);
    }

    public Command getDefaultCommand(Subsystem subsystem) {
        return m_subsystems.get(subsystem);
    }
}
