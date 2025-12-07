package org.firstinspires.ftc.teamcode.teleop.subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.hardware.motors.Motor;

public class driveSubsystem extends SubsystemBase {
    private MecanumDrive drive;
    private double strafeSpeed;
    private double forwardSpeed;
    private double turnSpeed;

    private double m_gamepad;
    public driveSubsystem(Motor lf, Motor rf, Motor lb, Motor rb, GamepadEx gamepad) {
        drive = new MecanumDrive(lf,rf,lb,rb);
        drive.setRightSideInverted(true);
        strafeSpeed = gamepad.getLeftX();
        forwardSpeed = gamepad.getRightX();
        turnSpeed = gamepad.getLeftX();
    }

    public void drive() {
        drive.driveRobotCentric(strafeSpeed,forwardSpeed,turnSpeed,false);
    }
}