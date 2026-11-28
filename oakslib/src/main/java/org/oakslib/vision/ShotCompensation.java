package org.oakslib.vision;

public final class ShotCompensation {

    public static final double DEFAULT_MIN_RPM = 0.0;
    public static final double DEFAULT_MAX_RPM = 6000.0;

    private ShotCompensation() {
    }

    public static Result compute(
            double stationaryRpm,
            double robotVx,
            double robotVy,
            double losAngleRad,
            double ballSpeedPerRpm) {
        return compute(
                stationaryRpm,
                robotVx,
                robotVy,
                losAngleRad,
                ballSpeedPerRpm,
                DEFAULT_MIN_RPM,
                DEFAULT_MAX_RPM);
    }

    public static Result compute(
            double stationaryRpm,
            double robotVx,
            double robotVy,
            double losAngleRad,
            double ballSpeedPerRpm,
            double minRpm,
            double maxRpm) {
        if (stationaryRpm <= 0.0 || ballSpeedPerRpm <= 0.0) {
            return new Result(0.0, stationaryRpm, 0.0, false);
        }

        double cos = Math.cos(losAngleRad);
        double sin = Math.sin(losAngleRad);
        double vAlong = robotVx * cos + robotVy * sin;
        double vRight = robotVx * sin - robotVy * cos;

        double stationarySpeed = stationaryRpm * ballSpeedPerRpm;
        double requiredAlong = stationarySpeed - vAlong;
        double requiredRight = -vRight;
        double requiredSpeed = Math.hypot(requiredAlong, requiredRight);

        boolean valid = requiredAlong > 0.0;
        double leadDegrees = valid
                ? Math.toDegrees(Math.atan2(requiredRight, requiredAlong))
                : 0.0;
        double rpm = valid
                ? stationaryRpm * requiredSpeed / stationarySpeed
                : maxRpm;

        rpm = Math.max(minRpm, Math.min(maxRpm, rpm));
        return new Result(leadDegrees, rpm, requiredSpeed, valid);
    }

    public static final class Result {

        private final double m_leadAngleDegrees;
        private final double m_rpm;
        private final double m_requiredSpeed;
        private final boolean m_valid;

        private Result(double leadAngleDegrees, double rpm, double requiredSpeed, boolean valid) {
            m_leadAngleDegrees = leadAngleDegrees;
            m_rpm = rpm;
            m_requiredSpeed = requiredSpeed;
            m_valid = valid;
        }

        public double getLeadAngleDegrees() {
            return m_leadAngleDegrees;
        }

        public double getRpm() {
            return m_rpm;
        }

        public double getRequiredSpeed() {
            return m_requiredSpeed;
        }

        public boolean isValid() {
            return m_valid;
        }
    }
}
