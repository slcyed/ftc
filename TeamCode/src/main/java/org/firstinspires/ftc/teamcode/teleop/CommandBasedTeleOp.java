package org.firstinspires.ftc.teamcode.teleop;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.commands.SetFlyWheel;
import org.oakslib.command.CommandBasedOpMode;
import org.oakslib.command.Trigger;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;
import org.oakslib.mecanum.MecanumDriveBase;
@TeleOp(group="TeleOp", name="Simpel")
public class CommandBasedTeleOp extends CommandBasedOpMode {
    @Override
    protected void initialize() {
        ShooterSubsystem flyWheelSubsystem = new ShooterSubsystem(hardwareMap.get(DcMotorEx.class,"centerMotor"));
        Trigger rightBumper = new Trigger(() -> gamepad1.rightBumperWasPressed());
        SetFlyWheel setFlywheel = new SetFlyWheel(flyWheelSubsystem,telemetry);
        rightBumper.onTrue(setFlywheel);
        hardwareMap.get(DcMotorEx.class,"frontLeftMotor").setDirection(DcMotorSimple.Direction.REVERSE);
        hardwareMap.get(DcMotorEx.class,"backLeftMotor").setDirection(DcMotorSimple.Direction.REVERSE);
        MecanumDriveBase mecanumDriveBase = new MecanumDriveBase(hardwareMap.get(DcMotorEx.class,"frontLeftMotor"),hardwareMap.get(DcMotorEx.class,"backLeftMotor"),hardwareMap.get(DcMotorEx.class,"frontRightMotor"),hardwareMap.get(DcMotorEx.class,"backRightMotor"),0,gamepad1);

    }
}