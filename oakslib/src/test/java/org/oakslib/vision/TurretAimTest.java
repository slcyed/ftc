package org.oakslib.vision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Set;

public class TurretAimTest {

    private static final double FOCAL_PX = 578.272;
    private static final double WIDTH_PX = 640.0;

    @Test
    public void centerOfImageHasZeroBearing() {
        assertEquals(0.0, TurretAim.bearingFromPixels(320, WIDTH_PX, FOCAL_PX), 1e-9);
    }

    @Test
    public void rightOfCenterHasPositiveBearing() {
        assertTrue(TurretAim.bearingFromPixels(480, WIDTH_PX, FOCAL_PX) > 0);
    }

    @Test
    public void leftOfCenterHasNegativeBearing() {
        assertTrue(TurretAim.bearingFromPixels(160, WIDTH_PX, FOCAL_PX) < 0);
    }

    @Test
    public void bearingIsSymmetricAboutCenter() {
        double right = TurretAim.bearingFromPixels(480, WIDTH_PX, FOCAL_PX);
        double left = TurretAim.bearingFromPixels(160, WIDTH_PX, FOCAL_PX);
        assertEquals(right, -left, 1e-9);
    }

    @Test
    public void zeroErrorProducesZeroPower() {
        assertEquals(0.0, TurretAim.power(0, 0.02, 0.5, 0, 1.0, false), 0);
    }

    @Test
    public void insideDeadbandProducesZeroPower() {
        assertEquals(0.0, TurretAim.power(0.5, 0.02, 0.5, 0, 1.0, false), 0);
        assertEquals(0.0, TurretAim.power(-0.5, 0.02, 0.5, 0, 1.0, false), 0);
    }

    @Test
    public void outputClipsAtMaxPower() {
        assertEquals(0.5, TurretAim.power(100, 0.02, 0.5, 0, 1.0, false), 1e-9);
        assertEquals(-0.5, TurretAim.power(-100, 0.02, 0.5, 0, 1.0, false), 1e-9);
    }

    @Test
    public void minPowerOvercomesStiction() {
        assertEquals(0.06, TurretAim.power(1.5, 0.001, 0.5, 0.06, 1.0, false), 1e-9);
        assertEquals(-0.06, TurretAim.power(-1.5, 0.001, 0.5, 0.06, 1.0, false), 1e-9);
    }

    @Test
    public void invertedFlipsOutputSign() {
        assertEquals(-0.5, TurretAim.power(100, 0.02, 0.5, 0, 1.0, true), 1e-9);
        assertEquals(0.5, TurretAim.power(-100, 0.02, 0.5, 0, 1.0, true), 1e-9);
    }

    @Test
    public void invertingDoesNotBreakDeadband() {
        assertEquals(0.0, TurretAim.power(0.5, 0.02, 0.5, 0, 1.0, true), 0);
    }

    @Test
    public void selectBestPicksSmallestAbsoluteBearing() {
        int[] ids = {30, 34, 38};
        double[] bearings = {-12.0, 3.0, -4.0};
        assertEquals(1, TurretAim.selectBestIndex(ids, bearings, Set.of()));
    }

    @Test
    public void selectBestIgnoresNonTargetTags() {
        int[] ids = {30, 34, 38};
        double[] bearings = {0.5, 9.0, 4.0};
        assertEquals(2, TurretAim.selectBestIndex(ids, bearings, Set.of(38)));
    }

    @Test
    public void selectBestReturnsNoneWhenNoTargetPresent() {
        int[] ids = {30, 34};
        double[] bearings = {1.0, 2.0};
        assertEquals(-1, TurretAim.selectBestIndex(ids, bearings, Set.of(45)));
    }

    @Test
    public void selectBestReturnsNoneForEmptyDetections() {
        assertEquals(-1, TurretAim.selectBestIndex(new int[0], new double[0], Set.of(30)));
    }
}
