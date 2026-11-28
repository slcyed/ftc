package org.firstinspires.ftc.teamcode.vision;

public final class BioBuzzTargets {

    public static final double FIELD_CENTER = 72.0;
    public static final double HIVE_X_OFFSET = 12.75;
    public static final double CELL_Y_OFFSET = 15.44;

    public static final double RED_X = FIELD_CENTER - HIVE_X_OFFSET;
    public static final double BLUE_X = FIELD_CENTER + HIVE_X_OFFSET;
    public static final double AUDIENCE_Y = FIELD_CENTER - CELL_Y_OFFSET;
    public static final double FAR_Y = FIELD_CENTER + CELL_Y_OFFSET;

    private BioBuzzTargets() {
    }

    public static double[] targetForTag(int tagId) {
        if (tagId >= 30 && tagId <= 33) {
            return new double[]{RED_X, FAR_Y};
        }
        if (tagId >= 34 && tagId <= 37) {
            return new double[]{RED_X, AUDIENCE_Y};
        }
        if (tagId >= 38 && tagId <= 41) {
            return new double[]{BLUE_X, AUDIENCE_Y};
        }
        if (tagId >= 42 && tagId <= 45) {
            return new double[]{BLUE_X, FAR_Y};
        }
        return null;
    }
}
