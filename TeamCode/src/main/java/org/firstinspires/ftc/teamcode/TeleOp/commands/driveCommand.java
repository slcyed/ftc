package org.firstinspires.ftc.teamcode.TeleOp.commands;
import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.TeleOp.subsystems.driveSubsystem;

public class driveCommand extends CommandBase {
    private final driveSubsystem m_driveSubsystem;
    public driveCommand(driveSubsystem driveSubsystem) {
        m_driveSubsystem = driveSubsystem;

        addRequirements(m_driveSubsystem);
    }

    public void execute(){
        m_driveSubsystem.drive();
    }
}
