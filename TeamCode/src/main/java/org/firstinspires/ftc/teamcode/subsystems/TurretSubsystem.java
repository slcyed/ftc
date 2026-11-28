package org.firstinspires.ftc.teamcode.subsystems;

import android.util.Size;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.oakslib.command.SubsystemBase;
import org.oakslib.vision.TurretAim;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TurretSubsystem extends SubsystemBase {

    public static final int[] RED_OPPOSITE_AUDIENCE = {30, 31, 32, 33};
    public static final int[] RED_AUDIENCE = {34, 35, 36, 37};
    public static final int[] BLUE_AUDIENCE = {38, 39, 40, 41};
    public static final int[] BLUE_OPPOSITE_AUDIENCE = {42, 43, 44, 45};
    public static final int[] ALL_BIOBUZZ_TAGS = {
            30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45
    };

    private static final double TAG_SIZE_INCHES = 6.5;
    private static final double DEFAULT_FOCAL_LENGTH_PX = 578.272;
    private static final double DEFAULT_IMAGE_WIDTH_PX = 640.0;
    private static final double DEFAULT_IMAGE_HEIGHT_PX = 480.0;

    private final CRServo m_servo;
    private final AprilTagProcessor m_aprilTag;
    private final VisionPortal m_visionPortal;
    private final Set<Integer> m_targetTagIds = new HashSet<>();

    private double m_kp = 0.02;
    private double m_maxPower = 0.5;
    private double m_minPower = 0.0;
    private double m_deadbandDeg = 1.0;
    private boolean m_inverted = false;
    private float m_minDecisionMargin = 10.0f;
    private double m_focalLengthPx = DEFAULT_FOCAL_LENGTH_PX;
    private double m_imageWidthPx = DEFAULT_IMAGE_WIDTH_PX;

    private boolean m_hasTarget = false;
    private int m_lastTagId = -1;
    private double m_lastBearingDeg = 0.0;
    private double m_lastRangeInches = -1.0;

    public TurretSubsystem(HardwareMap hardwareMap, String servoName, String webcamName, int... targetTagIds) {
        m_servo = hardwareMap.get(CRServo.class, servoName);
        setTargetTagIds(targetTagIds);

        AprilTagLibrary.Builder libraryBuilder = new AprilTagLibrary.Builder();
        for (int id : ALL_BIOBUZZ_TAGS) {
            libraryBuilder.addTag(id, "BIOBUZZ_" + id, TAG_SIZE_INCHES, DistanceUnit.INCH);
        }
        AprilTagLibrary library = libraryBuilder.build();

        m_aprilTag = new AprilTagProcessor.Builder()
                .setTagLibrary(library)
                .setDrawAxes(false)
                .setDrawCubeProjection(false)
                .setDrawTagID(false)
                .setDrawTagOutline(false)
                .build();
        m_aprilTag.setDecimation(2.0f);

        m_visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, webcamName))
                .setCameraResolution(new Size((int) DEFAULT_IMAGE_WIDTH_PX, (int) DEFAULT_IMAGE_HEIGHT_PX))
                .addProcessor(m_aprilTag)
                .build();
    }

    public void update() {
        updateDetection();
        aim(0.0);
    }

    public void update(double leadAngleDeg) {
        updateDetection();
        aim(leadAngleDeg);
    }

    public void updateDetection() {
        AprilTagSingleDetection target = findBestTarget();
        if (target == null) {
            m_hasTarget = false;
            m_lastRangeInches = -1.0;
            return;
        }

        m_hasTarget = true;
        m_lastTagId = target.id;
        m_lastBearingDeg = bearingDegrees(target);
        m_lastRangeInches = target.ftcPose != null ? target.ftcPose.range : -1.0;
    }

    public void aim(double leadAngleDeg) {
        if (!m_hasTarget) {
            m_servo.setPower(0);
            return;
        }

        m_servo.setPower(TurretAim.power(
                m_lastBearingDeg + leadAngleDeg,
                m_kp,
                m_maxPower,
                m_minPower,
                m_deadbandDeg,
                m_inverted));
    }

    private AprilTagSingleDetection findBestTarget() {
        List<AprilTagDetection> detections = m_aprilTag.getDetections();
        java.util.ArrayList<AprilTagSingleDetection> usable = new java.util.ArrayList<>();
        int[] ids = new int[detections.size()];
        double[] bearings = new double[detections.size()];
        int count = 0;
        for (AprilTagDetection detection : detections) {
            if (!(detection instanceof AprilTagSingleDetection)) {
                continue;
            }
            AprilTagSingleDetection singleDet = (AprilTagSingleDetection) detection;
            if (!isUsable(singleDet)) {
                continue;
            }
            ids[count] = singleDet.id;
            bearings[count] = bearingDegrees(singleDet);
            usable.add(singleDet);
            count++;
        }
        int bestIndex = TurretAim.selectBestIndex(
                java.util.Arrays.copyOf(ids, count),
                java.util.Arrays.copyOf(bearings, count),
                m_targetTagIds);
        return bestIndex < 0 ? null : usable.get(bestIndex);
    }

    private boolean isUsable(AprilTagSingleDetection detection) {
        return detection != null && detection.decisionMargin >= m_minDecisionMargin;
    }

    private double bearingDegrees(AprilTagSingleDetection detection) {
        if (detection.ftcPose != null) {
            return detection.ftcPose.bearing;
        }
        if (detection.center == null) {
            return 0.0;
        }
        return TurretAim.bearingFromPixels(detection.center.x, m_imageWidthPx, m_focalLengthPx);
    }

    public void stop() {
        m_servo.setPower(0);
    }

    public void close() {
        stop();
        m_visionPortal.close();
    }

    public void setTargetTagIds(int... targetTagIds) {
        m_targetTagIds.clear();
        for (int id : targetTagIds) {
            m_targetTagIds.add(id);
        }
    }

    public void setTargetCluster(int[] cluster) {
        setTargetTagIds(cluster);
    }

    public void setLoopGains(double kp, double maxPower, double deadbandDeg) {
        m_kp = kp;
        m_maxPower = maxPower;
        m_deadbandDeg = deadbandDeg;
    }

    public void setMinimumPower(double minPower) {
        m_minPower = minPower;
    }

    public void setMinDecisionMargin(float minDecisionMargin) {
        m_minDecisionMargin = minDecisionMargin;
    }

    public void setInverted(boolean inverted) {
        m_inverted = inverted;
    }

    public void setCameraIntrinsics(double focalLengthPx, double imageWidthPx) {
        m_focalLengthPx = focalLengthPx;
        m_imageWidthPx = imageWidthPx;
    }

    public boolean hasTarget() {
        return m_hasTarget;
    }

    public int getLastTagId() {
        return m_lastTagId;
    }

    public double getLastBearingDegrees() {
        return m_lastBearingDeg;
    }

    public double getLastRangeInches() {
        return m_lastRangeInches;
    }

    public VisionPortal getVisionPortal() {
        return m_visionPortal;
    }
}
