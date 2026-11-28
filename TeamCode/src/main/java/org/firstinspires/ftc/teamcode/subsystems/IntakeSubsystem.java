package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.oakslib.command.SubsystemBase;
import org.oakslib.controllers.PIDFController;

public class IntakeSubsystem extends SubsystemBase {

    private static final double DEGREES_PER_SECOND_PER_RPM = 6.0;

    private final DcMotorEx m_intakeMotor;
    private final CRServo m_rightIntakeServo;
    private final CRServo m_leftIntakeServo;
    private final CRServo m_transferServo;

    public IntakeSubsystem(DcMotorEx intakeMotor, CRServo rightIntakeServo, CRServo leftIntakeServo, CRServo transferServo) {
        m_intakeMotor = intakeMotor;
        m_rightIntakeServo = rightIntakeServo;
        m_leftIntakeServo = leftIntakeServo;
        m_transferServo = transferServo;
    }
    public void startIntake() {
        m_intakeMotor.setPower(.7);
        m_rightIntakeServo.setPower(-1);
        m_leftIntakeServo.setPower(1);

    }
    public void transferOn() {
        m_transferServo.setPower(-1);
    }
    public void transferOff() {
        m_transferServo.setPower(0);
    }
    public void stopIntake() {
        m_intakeMotor.setPower(0);
        m_rightIntakeServo.setPower(0);
        m_leftIntakeServo.setPower(0);
    }
}
