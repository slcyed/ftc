package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.oakslib.command.SubsystemBase;
import org.firstinspires.ftc.teamcode.oakslib.controllers.PIDFController;

public class ShooterSubsystem extends SubsystemBase {

    private final DcMotorEx m_flyWheel;
    private final PIDFController m_flyWheelController = new PIDFController(0.01, 0, 0.001, 0.05);;

    public ShooterSubsystem(DcMotorEx flyWheel) {
        m_flyWheel = flyWheel;
    }

    public void updateFlyWheel(double power) {
        m_flyWheel.setPower((m_flyWheelController.calculate(power*6000, (m_flyWheel.getVelocity(AngleUnit.DEGREES)/360)))/6000);
    }
}
