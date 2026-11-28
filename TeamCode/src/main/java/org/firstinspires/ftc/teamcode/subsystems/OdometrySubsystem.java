package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.ftc.localization.localizers.DriveEncoderLocalizer;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.Vector;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.oakslib.command.SubsystemBase;

public class OdometrySubsystem extends SubsystemBase {

    private static final double WHEEL_DIAMETER_IN = 3.7795;
    private static final double TICKS_PER_REV = 537.6;
    private static final double INCHES_PER_TICK = Math.PI * WHEEL_DIAMETER_IN / TICKS_PER_REV;
    private static final double ROBOT_WIDTH_IN = 16.0;
    private static final double ROBOT_LENGTH_IN = 16.0;

    private final DriveEncoderLocalizer m_localizer;

    public OdometrySubsystem(HardwareMap hardwareMap) {
        DriveEncoderConstants constants = new DriveEncoderConstants()
                .leftFrontMotorName("frontLeftMotor")
                .rightFrontMotorName("frontRightMotor")
                .leftRearMotorName("backLeftMotor")
                .rightRearMotorName("backRightMotor")
                .forwardTicksToInches(INCHES_PER_TICK / 4.0)
                .strafeTicksToInches(INCHES_PER_TICK / 4.0)
                .turnTicksToInches(INCHES_PER_TICK / 2.0)
                .robotWidth(ROBOT_WIDTH_IN)
                .robotLength(ROBOT_LENGTH_IN)
                .leftFrontEncoderDirection(Encoder.FORWARD)
                .rightFrontEncoderDirection(Encoder.REVERSE)
                .leftRearEncoderDirection(Encoder.FORWARD)
                .rightRearEncoderDirection(Encoder.REVERSE);
        m_localizer = new DriveEncoderLocalizer(hardwareMap, constants);
    }

    public void update() {
        m_localizer.update();
    }

    public Pose getPose() {
        return m_localizer.getPose();
    }

    public Vector getVelocity() {
        return m_localizer.getVelocityVector();
    }

    public void setStartPose(double x, double y, double headingRad) {
        m_localizer.setStartPose(new Pose(x, y, headingRad));
    }

    public double getHeadingRad() {
        return m_localizer.getPose().getHeading();
    }
}
