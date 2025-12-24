package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.sun.tools.doclint.Messages;

import org.firstinspires.ftc.teamcode.commands.SetFlyWheel;
import org.firstinspires.ftc.teamcode.oakslib.command.CommandBasedOpMode;
import org.firstinspires.ftc.teamcode.oakslib.command.CommandScheduler;
import org.firstinspires.ftc.teamcode.oakslib.command.Subsystem;
import org.firstinspires.ftc.teamcode.oakslib.command.SubsystemBase;
import org.firstinspires.ftc.teamcode.oakslib.command.Trigger;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;

@TeleOp(group="TeleOp", name="CommandBasedSample")
public class CommandBasedTeleOp extends CommandBasedOpMode {

    @Override
    public void init() {
        ShooterSubsystem flyWheelSubsystem = new ShooterSubsystem(hardwareMap.get(DcMotorEx.class,"centerMotor"));
        Trigger rightBumper = new Trigger(() -> gamepad1.right_bumper);
        rightBumper.onTrue(new SetFlyWheel(1,flyWheelSubsystem));
        rightBumper.onFalse(new SetFlyWheel(0,flyWheelSubsystem));

        DriveSubsystem driveSubsystem;
        CommandScheduler.getInstance().setDefaultCommand(driveSubsystem);
    }
}
