package org.firstinspires.ftc.teamcode.TeleOp.commands;
import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.TeleOp.subsystems.shooterSubsystem;

public class toggleFlyWheelCommand extends CommandBase {
    private final shooterSubsystem m_shooterSubsystem;

    /**
     * Creates a new ExampleCommand.
     *
     * @param subsystem The subsystem used by this command.
     */
    public toggleFlyWheelCommand(shooterSubsystem shooterSubsystem) {
        m_shooterSubsystem = shooterSubsystem;
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(shooterSubsystem);
    }
    public void execute(){
        m_shooterSubsystem.toggleSpeed();
    }
}
