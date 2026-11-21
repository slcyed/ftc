package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.oakslib.command.SubsystemBase;
import org.oakslib.controllers.PIDFController;

public class ShooterSubsystem extends SubsystemBase {

    private static final double DEGREES_PER_SECOND_PER_RPM = 6.0;

    private final DcMotorEx m_flyWheel;
    private final PIDFController m_pid = new PIDFController(0.0001, 0.0, 0.0, 0.00017);

    private double m_targetRpm = 0.0;
    private double m_powerSign = 1.0;
    private double m_lastOutput = 0.0;

    public ShooterSubsystem(DcMotorEx flyWheel) {
        m_flyWheel = flyWheel;
    }

    public void updateFlyWheel(double power) {
        m_lastOutput = power;
        m_flyWheel.setPower(power);
    }

    public void setTargetRpm(double rpm) {
        m_targetRpm = rpm;
    }

    public double getTargetRpm() {
        return m_targetRpm;
    }

    public double getVelocityRpm() {
        return m_powerSign * m_flyWheel.getVelocity(AngleUnit.DEGREES) / DEGREES_PER_SECOND_PER_RPM;
    }

    public void setPidGains(double kP, double kI, double kD, double kF) {
        m_pid.setkP(kP);
        m_pid.setkI(kI);
        m_pid.setkD(kD);
        m_pid.setkF(kF);
    }

    public void setPowerSign(double powerSign) {
        m_powerSign = powerSign;
    }

    public void resetPid() {
        m_pid.reset();
    }

    public void update() {
        double output = m_pid.calculate(m_targetRpm, getVelocityRpm());
        output = Math.max(-1.0, Math.min(1.0, output));
        m_lastOutput = output * m_powerSign;
        m_flyWheel.setPower(m_lastOutput);
    }

    public double getLastOutput() {
        return m_lastOutput;
    }

    public void stop() {
        m_lastOutput = 0.0;
        m_flyWheel.setPower(0);
    }
}
