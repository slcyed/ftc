package org.oakslib.vision;

import java.util.Set;

public final class TurretAim {

    private TurretAim() {
    }

    public static double bearingFromPixels(double centerX, double imageWidthPx, double focalLengthPx) {
        double offsetPx = centerX - (imageWidthPx / 2.0);
        return Math.toDegrees(Math.atan2(offsetPx, focalLengthPx));
    }

    public static int selectBestIndex(int[] ids, double[] bearings, Set<Integer> targetIds) {
        int bestIndex = -1;
        double bestAbsBearing = Double.MAX_VALUE;
        for (int i = 0; i < ids.length; i++) {
            if (targetIds != null && !targetIds.isEmpty() && !targetIds.contains(ids[i])) {
                continue;
            }
            double absBearing = Math.abs(bearings[i]);
            if (absBearing < bestAbsBearing) {
                bestAbsBearing = absBearing;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    public static double power(
            double bearingDeg,
            double kp,
            double maxPower,
            double minPower,
            double deadbandDeg,
            boolean inverted) {
        if (Math.abs(bearingDeg) <= deadbandDeg) {
            return 0.0;
        }
        double output = kp * bearingDeg;
        output = Math.max(-maxPower, Math.min(maxPower, output));
        if (Math.abs(output) < minPower) {
            output = Math.copySign(minPower, output);
        }
        if (inverted) {
            output = -output;
        }
        return output;
    }
}
