package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;

@TeleOp(name = "Shooter Tuner", group = "Tuning")
public class ShooterTuner extends LinearOpMode {

    private static final double[] DISTANCES_IN = {24, 48, 72, 96, 120};
    private static final double[] TABLE_RPMS = {2400, 2700, 3000, 3300, 3600};

    private static final int MODE_FLYWHEEL = 0;
    private static final int MODE_PID = 1;
    private static final int MODE_TABLE = 2;
    private static final String[] MODE_NAMES = {"FLYWHEEL", "PID", "SHOT TABLE"};

    private static final double TARGET_STEP = 50.0;
    private static final double KP_STEP = 0.00001;
    private static final double KI_STEP = 0.0000001;
    private static final double KD_STEP = 0.000001;
    private static final double KF_STEP = 0.00001;
    private static final double RPM_STEP = 25.0;

    private final ElapsedTime m_holdTimer = new ElapsedTime();

    @Override
    public void runOpMode() {
        DcMotorEx centerMotor = hardwareMap.get(DcMotorEx.class, "centerMotor");
        centerMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ShooterSubsystem shooter = new ShooterSubsystem(centerMotor);

        double targetRpm = 3000.0;
        double kP = 0.0001;
        double kI = 0.0;
        double kD = 0.0;
        double kF = 0.00017;
        double powerSign = 1.0;
        boolean enabled = false;
        boolean directMode = false;

        double[] distances = DISTANCES_IN.clone();
        double[] tableRpms = TABLE_RPMS.clone();

        int mode = MODE_FLYWHEEL;
        int selected = 0;

        telemetry.addLine("SHOOTER TUNER");
        telemetry.addLine("X page | A run | B reset | Y direction | LT/RT direct power");
        telemetry.addLine("D-pad up/down select, left/right adjust");
        telemetry.addLine("Hold LB x10 step, hold RB x0.1 step");
        telemetry.addLine("Put the robot on blocks before running the flywheel.");
        telemetry.update();

        waitForStart();
        m_holdTimer.reset();

        while (opModeIsActive()) {
            if (gamepad1.xWasPressed()) {
                mode = (mode + 1) % MODE_NAMES.length;
                selected = 0;
            }
            if (gamepad1.bWasPressed()) {
                shooter.resetPid();
            }
            if (gamepad1.yWasPressed()) {
                powerSign = -powerSign;
            }

            int rows = rowCount(mode, tableRpms.length);
            if (gamepad1.dpadUpWasPressed()) {
                selected = (selected - 1 + rows) % rows;
            }
            if (gamepad1.dpadDownWasPressed()) {
                selected = (selected + 1) % rows;
            }

            double scale = gamepad1.left_bumper ? 10.0 : (gamepad1.right_bumper ? 0.1 : 1.0);

            int change = 0;
            if (gamepad1.dpadLeftWasPressed()) {
                change = -1;
            }
            if (gamepad1.dpadRightWasPressed()) {
                change = 1;
            }
            if (gamepad1.dpad_left && m_holdTimer.milliseconds() > 400) {
                change = -1;
                m_holdTimer.reset();
            }
            if (gamepad1.dpad_right && m_holdTimer.milliseconds() > 400) {
                change = 1;
                m_holdTimer.reset();
            }
            if (!gamepad1.dpad_left && !gamepad1.dpad_right) {
                m_holdTimer.reset();
            }

            if (change != 0) {
                if (mode == MODE_FLYWHEEL) {
                    if (selected == 0) {
                        targetRpm = clamp(targetRpm + change * TARGET_STEP * scale, 0.0, 8000.0);
                        if (!enabled) {
                            enabled = true;
                            shooter.resetPid();
                        }
                    } else if (selected == 1) {
                        enabled = !enabled;
                        shooter.resetPid();
                    } else {
                        powerSign = -powerSign;
                    }
                } else if (mode == MODE_PID) {
                    if (selected == 0) {
                        kP = Math.max(0.0, kP + change * KP_STEP * scale);
                    } else if (selected == 1) {
                        kI = Math.max(0.0, kI + change * KI_STEP * scale);
                    } else if (selected == 2) {
                        kD = Math.max(0.0, kD + change * KD_STEP * scale);
                    } else if (selected == 3) {
                        kF = Math.max(0.0, kF + change * KF_STEP * scale);
                    } else {
                        shooter.resetPid();
                    }
                } else {
                    tableRpms[selected] = clamp(tableRpms[selected] + change * RPM_STEP * scale, 0.0, 8000.0);
                    targetRpm = tableRpms[selected];
                    if (!enabled) {
                        enabled = true;
                        shooter.resetPid();
                    }
                }
            }

            if (gamepad1.aWasPressed()) {
                enabled = !enabled;
                shooter.resetPid();
            }

            shooter.setPidGains(kP, kI, kD, kF);
            shooter.setPowerSign(powerSign);
            shooter.setTargetRpm(targetRpm);
            double directPower = gamepad1.right_trigger - gamepad1.left_trigger;
            boolean wantDirect = Math.abs(directPower) > 0.05;
            if (wantDirect != directMode) {
                centerMotor.setMode(wantDirect ? DcMotor.RunMode.RUN_WITHOUT_ENCODER : DcMotor.RunMode.RUN_USING_ENCODER);
                directMode = wantDirect;
            }
            if (directMode) {
                centerMotor.setPower(directPower * powerSign);
            } else if (enabled) {
                centerMotor.setVelocity(targetRpm * 6.0 * powerSign, AngleUnit.DEGREES);
            } else {
                centerMotor.setPower(0);
            }

            double actualRpm = (centerMotor.getVelocity() / 537.7) * 60.0 * (6000.0 / 312.0);
            double error = targetRpm - actualRpm;

            telemetry.addData("Page", MODE_NAMES[mode]);
            telemetry.addData("Flywheel", enabled ? "RUNNING" : "STOPPED");
            telemetry.addData("Target", "%.0f rpm", targetRpm);
            telemetry.addData("Actual", "%.0f rpm", actualRpm);
            telemetry.addData("Encoder", "%.0f deg/s", centerMotor.getVelocity(AngleUnit.DEGREES));
            telemetry.addData("Error", "%.0f rpm", error);
            telemetry.addData("Controller", directMode ? "DIRECT" : "BUILT-IN");
            telemetry.addData("Power", "%.3f", centerMotor.getPower());
            telemetry.addData("Current", "%.2f A", centerMotor.getCurrent(CurrentUnit.AMPS));
            telemetry.addData("Direction", powerSign > 0 ? "+1" : "-1");
            telemetry.addLine();
            addRows(telemetry, mode, selected, kP, kI, kD, kF, tableRpms);
            telemetry.addLine();
            telemetry.addData("Gains", "P %.6f  I %.7f  D %.6f  F %.6f", kP, kI, kD, kF);
            telemetry.addData("Table", tableString(distances, tableRpms));
            telemetry.addLine("X page | A run | B reset | Y direction | LT/RT direct power");
            telemetry.update();
        }

        shooter.stop();
    }

    private static int rowCount(int mode, int tableLength) {
        if (mode == MODE_FLYWHEEL) {
            return 3;
        }
        if (mode == MODE_PID) {
            return 5;
        }
        return tableLength;
    }

    private static void addRows(Telemetry telemetry, int mode, int selected,
                                double kP, double kI, double kD, double kF, double[] tableRpms) {
        if (mode == MODE_FLYWHEEL) {
            telemetry.addData(marker(selected, 0), "Target RPM");
            telemetry.addData(marker(selected, 1), "Toggle enabled");
            telemetry.addData(marker(selected, 2), "Toggle direction");
        } else if (mode == MODE_PID) {
            telemetry.addData(marker(selected, 0), "kP  %.6f", kP);
            telemetry.addData(marker(selected, 1), "kI  %.7f", kI);
            telemetry.addData(marker(selected, 2), "kD  %.6f", kD);
            telemetry.addData(marker(selected, 3), "kF  %.6f", kF);
            telemetry.addData(marker(selected, 4), "Reset PID");
        } else {
            for (int i = 0; i < tableRpms.length; i++) {
                telemetry.addData(marker(selected, i), "RPM %.0f", tableRpms[i]);
            }
        }
    }

    private static String marker(int selected, int index) {
        return (selected == index ? "> " : "  ") + index;
    }

    private static String tableString(double[] distances, double[] rpms) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < distances.length; i++) {
            sb.append(String.format("%.0f:%.0f", distances[i], rpms[i]));
            if (i < distances.length - 1) {
                sb.append("  ");
            }
        }
        return sb.toString();
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
