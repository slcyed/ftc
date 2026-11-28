package org.oakslib.vision;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ShotSpeedTest {

    private static final double[] DISTANCES = {24, 48, 72, 96, 120};
    private static final double[] RPMS = {2400, 2700, 3000, 3300, 3600};

    @Test
    public void clampsBelowFirstPoint() {
        assertEquals(2400, ShotSpeed.rpmForRange(10, DISTANCES, RPMS), 1e-9);
    }

    @Test
    public void clampsAboveLastPoint() {
        assertEquals(3600, ShotSpeed.rpmForRange(200, DISTANCES, RPMS), 1e-9);
    }

    @Test
    public void returnsExactTableValue() {
        assertEquals(3000, ShotSpeed.rpmForRange(72, DISTANCES, RPMS), 1e-9);
    }

    @Test
    public void interpolatesBetweenPoints() {
        assertEquals(2550, ShotSpeed.rpmForRange(36, DISTANCES, RPMS), 1e-9);
        assertEquals(3450, ShotSpeed.rpmForRange(108, DISTANCES, RPMS), 1e-9);
    }

    @Test
    public void interpolatesDecreasingTable() {
        double[] distances = {0, 10};
        double[] rpms = {5000, 4000};
        assertEquals(4500, ShotSpeed.rpmForRange(5, distances, rpms), 1e-9);
    }

    @Test
    public void handlesSinglePointTable() {
        assertEquals(2800, ShotSpeed.rpmForRange(50, new double[]{50}, new double[]{2800}), 1e-9);
    }

    @Test
    public void invalidTablesReturnZero() {
        assertEquals(0.0, ShotSpeed.rpmForRange(50, new double[0], new double[0]), 0);
        assertEquals(0.0, ShotSpeed.rpmForRange(50, new double[]{10, 20}, new double[]{3000}), 0);
        assertEquals(0.0, ShotSpeed.rpmForRange(50, null, null), 0);
    }
}
