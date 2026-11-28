package org.firstinspires.ftc.teamcode.teleop;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.commands.SetFlyWheel;
import org.firstinspires.ftc.teamcode.commands.starter.ToggleIntake;
import org.firstinspires.ftc.teamcode.commands.starter.ToggleTransfer;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.oakslib.command.CommandBasedOpMode;
import org.oakslib.command.Trigger;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;
import org.oakslib.hardware.MotorPlus;
import org.oakslib.mecanum.MecanumDriveBase;
@TeleOp(group="TeleOp", name="StarterBotTeleOp")
public class StarterBotTeleOp extends CommandBasedOpMode {
    @Override
    protected void initialize() {

        // Initialize Subsystems
        ShooterSubsystem flyWheelSubsystem = new ShooterSubsystem(hardwareMap.get(DcMotorEx.class,"centerMotor"));
        IntakeSubsystem intakeSubsystem = new IntakeSubsystem(hardwareMap.get(DcMotorEx.class, "intakeMotor"), hardwareMap.get(CRServo.class, "intakeServoL"), hardwareMap.get(CRServo.class, "intakeServoR"), hardwareMap.get(CRServo.class, "innerServo"));

        // Create Button Triggers
        Trigger rightBumper = new Trigger(() -> gamepad1.rightBumperWasPressed());
        Trigger leftStickPressed = new Trigger(() -> gamepad1.leftStickButtonWasPressed());
        Trigger rightStickPressed = new Trigger(() -> gamepad1.rightStickButtonWasPressed());

        // Create Commands
        SetFlyWheel setFlywheel = new SetFlyWheel(flyWheelSubsystem,telemetry);
        ToggleIntake toggleIntake = new ToggleIntake(intakeSubsystem,telemetry);
        ToggleTransfer toggleTransfer = new ToggleTransfer(intakeSubsystem,telemetry);

        // Bind Triggers to Commands
        rightBumper.onTrue(setFlywheel);
        leftStickPressed.onTrue(toggleIntake);
        rightStickPressed.onTrue(toggleTransfer);

        // Invert Left Side Motors
        hardwareMap.get(DcMotorEx.class,"frontLeftMotor").setDirection(DcMotorSimple.Direction.REVERSE);
        hardwareMap.get(DcMotorEx.class,"backLeftMotor").setDirection(DcMotorSimple.Direction.REVERSE);

        // Create Mecanum Drive Base
        MecanumDriveBase mecanumDriveBase = new MecanumDriveBase(new MotorPlus(hardwareMap.get(DcMotorEx.class,"frontLeftMotor")),new MotorPlus(hardwareMap.get(DcMotorEx.class,"backLeftMotor")),new MotorPlus(hardwareMap.get(DcMotorEx.class,"frontRightMotor")),new MotorPlus(hardwareMap.get(DcMotorEx.class,"backRightMotor")),0,gamepad1);
    }
}