package org.oakslib.vision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ShotCompensationTest {

    private static final double RPM = 3000.0;
    private static final double SPEED_PER_RPM = 0.1;
    private static final double STATIONARY_SPEED = RPM * SPEED_PER_RPM;

    @Test
    public void stationaryRobotKeepsTableValues() {
        ShotCompensation.Result result = compute(0, 0, 0);
        assertTrue(result.isValid());
        assertEquals(0.0, result.getLeadAngleDegrees(), 1e-9);
        assertEquals(RPM, result.getRpm(), 1e-9);
        assertEquals(STATIONARY_SPEED, result.getRequiredSpeed(), 1e-9);
    }

    @Test
    public void movingTowardTargetReducesRpm() {
        ShotCompensation.Result result = compute(30, 0, 0);
        assertTrue(result.isValid());
        assertEquals(0.0, result.getLeadAngleDegrees(), 1e-9);
        assertEquals(2700.0, result.getRpm(), 1e-9);
    }

    @Test
    public void movingAwayFromTargetIncreasesRpm() {
        ShotCompensation.Result result = compute(-30, 0, 0);
        assertTrue(result.isValid());
        assertEquals(0.0, result.getLeadAngleDegrees(), 1e-9);
        assertEquals(3300.0, result.getRpm(), 1e-9);
    }

    @Test
    public void movingRightLeadsLeft() {
        ShotCompensation.Result result = compute(0, -30, 0);
        assertTrue(result.isValid());
        assertEquals(Math.toDegrees(Math.atan2(-30, STATIONARY_SPEED)),
                result.getLeadAngleDegrees(), 1e-9);
        assertEquals(RPM * Math.hypot(STATIONARY_SPEED, 30) / STATIONARY_SPEED,
                result.getRpm(), 1e-9);
    }

    @Test
    public void movingLeftLeadsRight() {
        ShotCompensation.Result result = compute(0, 30, 0);
        assertTrue(result.isValid());
        assertEquals(Math.toDegrees(Math.atan2(30, STATIONARY_SPEED)),
                result.getLeadAngleDegrees(), 1e-9);
    }

    @Test
    public void diagonalMotionCombinesLeadAndRpm() {
        ShotCompensation.Result result = compute(20, -15, 0);
        assertTrue(result.isValid());
        assertEquals(Math.toDegrees(Math.atan2(-15, STATIONARY_SPEED - 20)),
                result.getLeadAngleDegrees(), 1e-9);
        assertEquals(RPM * Math.hypot(STATIONARY_SPEED - 20, 15) / STATIONARY_SPEED,
                result.getRpm(), 1e-9);
    }

    @Test
    public void usesLineOfSightAngle() {
        double los = Math.PI / 2.0;
        ShotCompensation.Result result = compute(0, 20, los);
        assertTrue(result.isValid());
        assertEquals(0.0, result.getLeadAngleDegrees(), 1e-9);
        assertEquals(2800.0, result.getRpm(), 1e-9);
    }

    @Test
    public void rejectsShotsThatCannotReachTarget() {
        ShotCompensation.Result result = compute(400, 0, 0, SPEED_PER_RPM, 0.0, 6000.0);
        assertFalse(result.isValid());
        assertEquals(6000.0, result.getRpm(), 1e-9);
    }

    @Test
    public void clampsCompensatedRpm() {
        ShotCompensation.Result result = compute(-3000, 0, 0, SPEED_PER_RPM, 0.0, 6000.0);
        assertTrue(result.isValid());
        assertEquals(6000.0, result.getRpm(), 1e-9);
    }

    @Test
    public void invalidBallCalibrationReturnsTableRpm() {
        ShotCompensation.Result result = compute(10, 0, 0, 0.0, 0.0, 6000.0);
        assertFalse(result.isValid());
        assertEquals(RPM, result.getRpm(), 1e-9);
        assertEquals(0.0, result.getLeadAngleDegrees(), 1e-9);
    }

    private static ShotCompensation.Result compute(double vx, double vy, double los) {
        return ShotCompensation.compute(RPM, vx, vy, los, SPEED_PER_RPM);
    }

    private static ShotCompensation.Result compute(
            double vx, double vy, double los, double speedPerRpm, double minRpm, double maxRpm) {
        return ShotCompensation.compute(RPM, vx, vy, los, speedPerRpm, minRpm, maxRpm);
    }
}
