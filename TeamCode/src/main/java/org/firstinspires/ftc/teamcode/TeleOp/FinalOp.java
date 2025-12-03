// Wifi password is "oaksoaks"

package org.firstinspires.ftc.teamcode.TeleOp;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.button.Button;
import com.arcrobotics.ftclib.command.button.GamepadButton;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;


import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import org.firstinspires.ftc.teamcode.TeleOp.commands.shootCommand;
import org.firstinspires.ftc.teamcode.TeleOp.commands.toggleFlyWheelCommand;
import org.firstinspires.ftc.teamcode.TeleOp.subsystems.driveSubsystem;
import org.firstinspires.ftc.teamcode.TeleOp.commands.driveCommand;
import org.firstinspires.ftc.teamcode.TeleOp.subsystems.shooterSubsystem;

@TeleOp

public class FinalOp extends CommandOpMode {
    public void initialize() {

        Motor lf = new Motor(hardwareMap, "frontLeftMotor", Motor.GoBILDA.RPM_1150);
        Motor rf = new Motor(hardwareMap, "frontRightMotor", Motor.GoBILDA.RPM_1150);
        Motor lb = new Motor(hardwareMap, "backLeftMotor", Motor.GoBILDA.RPM_1150);
        Motor rb = new Motor(hardwareMap, "backRightMotor", Motor.GoBILDA.RPM_1150);
        Motor flyWheelMotor = new Motor(hardwareMap, "centerMotor", Motor.GoBILDA.RPM_1150);

        GamepadEx gamepad = new GamepadEx(gamepad1);

        driveSubsystem driveSystem = new driveSubsystem(lf, rf, lb, rb, gamepad);

        shooterSubsystem shooterSystem = new shooterSubsystem(flyWheelMotor);

        Button trigger = new GamepadButton(gamepad, GamepadKeys.Button.RIGHT_BUMPER);

        trigger.whenPressed(new toggleFlyWheelCommand(shooterSystem));

        shooterSystem.setDefaultCommand(new shootCommand(shooterSystem));

        driveSystem.setDefaultCommand(new driveCommand(driveSystem));

    }
}