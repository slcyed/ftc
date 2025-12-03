package org.firstinspires.ftc.teamcode.TeleOp.subsystems;

import android.media.MediaScannerConnection;

import java.util.Optional;
import java.util.function.DoubleSupplier;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.Gamepad;

public class driveSubsystem extends SubsystemBase {
    private MecanumDrive drive;
    private DoubleSupplier strafeSpeed;
    private DoubleSupplier forwardSpeed;
    private DoubleSupplier turnSpeed;

    public driveSubsystem(Motor lf, Motor rf, Motor lb, Motor rb, GamepadEx gamepad) {
        MecanumDrive drive = new MecanumDrive(lf,rf,lb,rb);
        strafeSpeed = gamepad::getLeftX;
        forwardSpeed = gamepad::getLeftY;
        turnSpeed = gamepad::getRightX;
        drive.setRightSideInverted(true);
    }

    public void drive() {
        drive.driveRobotCentric(strafeSpeed.getAsDouble(), forwardSpeed.getAsDouble(), turnSpeed.getAsDouble(), false);
    }

    public void driveForDistance() {

    }
}