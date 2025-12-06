package org.firstinspires.ftc.teamcode.TeleOp.subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.motors.Motor;

public class shooterSubsystem extends SubsystemBase {
    private final Motor m_flyWheelMotor;
    private double m_speed = 0;
    public shooterSubsystem(Motor flyWheelMotor) {
        m_flyWheelMotor = flyWheelMotor;
    }

    public void toggleSpeed() {
        if (m_speed == .75) {
            m_speed = 0;
        } else {
            m_speed = -.75;
        }
        m_flyWheelMotor.set(m_speed);
    }
}