package org.firstinspires.ftc.teamcode.teleop;

import com.arcrobotics.ftclib.command.button.Button;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.teleop.subsystems.shooterSubsystem;

@TeleOp
public class SimpleTeleOp extends CommandOpMode {

    MecanumDrive mecanumDrive;

    shooterSubsystem shooterSystem;
    GamepadEx driverOp;

    Button rightBumper;

    @Override
    public void initialize() {
        shooterSystem = new shooterSubsystem(new Motor(hardwareMap, "centerMotor"));
        driverOp = new GamepadEx(gamepad1);
        rightBumper = driverOp.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER);
//        rightBumper.whenPressed(new InstantCommand(shooterSystem::toggleSpeed,shooterSystem));
        mecanumDrive = new MecanumDrive(new Motor(hardwareMap, "frontLeftMotor", Motor.GoBILDA.RPM_1150), new Motor(hardwareMap, "frontRightMotor", Motor.GoBILDA.RPM_1150), new Motor(hardwareMap, "backLeftMotor", Motor.GoBILDA.RPM_1150), new Motor(hardwareMap, "backRightMotor", Motor.GoBILDA.RPM_1150));
        // update telemetry every loop
        schedule(new RunCommand(telemetry::update));
    }
//    public void run() {
//      mecanumDrive.driveRobotCentric(driverOp.getLeftX(),
//                driverOp.getLeftY(),
//                driverOp.getRightX());
//    }
}