package org.oakslib.vision;

public final class ShotSpeed {

    private ShotSpeed() {
    }

    public static double rpmForRange(double rangeInches, double[] distances, double[] rpms) {
        if (distances == null || rpms == null || distances.length == 0 || distances.length != rpms.length) {
            return 0.0;
        }
        if (rangeInches <= distances[0]) {
            return rpms[0];
        }
        int last = distances.length - 1;
        if (rangeInches >= distances[last]) {
            return rpms[last];
        }
        for (int i = 0; i < last; i++) {
            if (rangeInches <= distances[i + 1]) {
                double span = distances[i + 1] - distances[i];
                double t = span == 0.0 ? 0.0 : (rangeInches - distances[i]) / span;
                return rpms[i] + t * (rpms[i + 1] - rpms[i]);
            }
        }
        return rpms[last];
    }
}
