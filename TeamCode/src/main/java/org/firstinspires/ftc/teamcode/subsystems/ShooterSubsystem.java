package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.oakslib.command.SubsystemBase;
import org.firstinspires.ftc.teamcode.oakslib.controllers.PIDFController;

public class ShooterSubsystem extends SubsystemBase {

    private final DcMotorEx m_flyWheel;

    public ShooterSubsystem(DcMotorEx flyWheel) {
        m_flyWheel = flyWheel;
    }

    public void updateFlyWheel(double power) {
        m_flyWheel.setPower(power);
    }
}
