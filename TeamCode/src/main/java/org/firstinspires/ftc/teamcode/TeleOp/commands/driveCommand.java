package org.firstinspires.ftc.teamcode.TeleOp.commands;
import com.arcrobotics.ftclib.command.CommandBase;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.gamepad.GamepadEx;

import org.firstinspires.ftc.teamcode.TeleOp.subsystems.driveSubsystem;

public class driveCommand extends RunCommand {
    public driveCommand(driveSubsystem driveSystem) {
        super(driveSystem::drive,driveSystem);
    }
}
