// Wifi password is "oaksoaks"

package org.firstinspires.ftc.teamcode.teleop;


import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.hardware.motors.CRServo;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.Vector;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.OdometrySubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;

@TeleOp

public class StarterBotTeleop extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
//    private CRServo armServo = null;
//    private Servo clawServo = null;
    private double armServoPower = .5;

    private double turnAngle = 0;

    private static final double[] SHOT_DISTANCES_IN = {24, 48, 72, 96, 120};
    private static final double[] SHOT_RPMS = {2400, 2700, 3000, 3300, 3600};

    private static final double START_X = 72.0;
    private static final double START_Y = 24.0;
    private static final double START_HEADING_RAD = Math.PI / 2.0;
    private static final double BALL_SPEED_PER_RPM = 0.10;
    private static final double MIN_RPM = 0.0;
    private static final double MAX_RPM = 6000.0;

    @Override
    public void runOpMode() {
        Motor lf = new Motor(hardwareMap, "frontLeftMotor", Motor.GoBILDA.RPM_312);
        Motor rf = new Motor(hardwareMap, "frontRightMotor", Motor.GoBILDA.RPM_312);
        Motor lb = new Motor(hardwareMap, "backLeftMotor", Motor.GoBILDA.RPM_312);
        Motor rb = new Motor(hardwareMap, "backRightMotor", Motor.GoBILDA.RPM_312);
        Motor intakeMotor = new Motor(hardwareMap, "intakeMotor", Motor.GoBILDA.RPM_312);

        boolean autoMode = false;
        CRServo intakeServoR = new CRServo(hardwareMap, "intakeServoR");
        CRServo intakeServoL = new CRServo(hardwareMap, "intakeServoL");
        CRServo innerServo = new CRServo(hardwareMap, "innerServo");

//        armServo.setPosition(.5);

        MecanumDrive drive = new MecanumDrive(
                lf,
                rf,
                lb,
                rb
        );
        drive.setRightSideInverted(true);
        OdometrySubsystem odometry = new OdometrySubsystem(hardwareMap);
        odometry.setStartPose(START_X, START_Y, START_HEADING_RAD);
        DcMotorEx centerMotor = hardwareMap.get(DcMotorEx.class, "centerMotor");
        centerMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        ShooterSubsystem shooter = new ShooterSubsystem(centerMotor);

        IMU imu = hardwareMap.get(IMU.class, "imu");
//        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(LogoFacingDirection.LEFT)));
//        TurretSubsystem turret = new TurretSubsystem(
//                hardwareMap,
//                "turretServo",
//                "Webcam 1",
//                TurretSubsystem.ALL_BIOBUZZ_TAGS
//        );

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        double centerMotorPower = 0.7;
        boolean autoAim = false;

        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            odometry.update();
            Pose pose = odometry.getPose();
            Vector velocity = odometry.getVelocity();
            double leadDeg = 0.0;
            double compensatedRpm = 0.0;
            boolean compensated = false;

            // Set Fly Wheel Motor Power when Right Bumper is Pressed
            if (gamepad1.dpadUpWasPressed()) {
                centerMotorPower+=.05;
            }
            if (gamepad1.dpadDownWasPressed()) {
                centerMotorPower-=.05;
            }
            shooter.updateFlyWheel(centerMotorPower);
            if (gamepad1.bWasPressed()) {
                if (intakeServoR.get()==0) {
                    intakeServoR.set(1);
                    intakeServoL.set(-1);
                } else {
                    intakeServoR.set(0);
                    intakeServoL.set(0);
                }
            }
            if (gamepad1.xWasPressed()) {
                if (innerServo.get()==0) {
                    innerServo.set(-1);
                } else {
                    innerServo.set(0);
                }
            }

            if (gamepad1.aWasPressed()) {
                if (intakeMotor.get()==0) {
                    intakeMotor.set(.7);
                } else {
                    intakeMotor.set(0);
                }
            }
            if (gamepad1.yWasPressed()) {
                autoAim = !autoAim;
                shooter.resetPid();
            }
//            if (autoAim) {
//                turret.updateDetection();
//                if (turret.hasTarget() && turret.getLastRangeInches() > 0) {
//                    double[] target = BioBuzzTargets.targetForTag(turret.getLastTagId());
//                    double baseRpm = ShotSpeed.rpmForRange(
//                            turret.getLastRangeInches(),
//                            SHOT_DISTANCES_IN,
//                            SHOT_RPMS);
//                    if (target != null) {
//                        double los = Math.atan2(
//                                target[1] - pose.getY(),
//                                target[0] - pose.getX());
//                        ShotCompensation.Result compensation = ShotCompensation.compute(
//                                baseRpm,
//                                velocity.getXComponent(),
//                                velocity.getYComponent(),
//                                los,
//                                BALL_SPEED_PER_RPM,
//                                MIN_RPM,
//                                MAX_RPM);
//                        leadDeg = compensation.getLeadAngleDegrees();
//                        compensatedRpm = compensation.getRpm();
//                        compensated = compensation.isValid();
//                    }
//                    shooter.setTargetRpm(target != null ? compensatedRpm : baseRpm);
//                }
//                turret.aim(leadDeg);
//                shooter.update();
//            } else {
//                turret.stop();
//                shooter.updateFlyWheel(centerMotorPower);
//            }
//            centerMotor.setPower(centerMotorPower);
            turnAngle = gamepad1.right_stick_x;

            drive.driveRobotCentric(
                    gamepad1.left_stick_x,
                    -gamepad1.left_stick_y,
                    turnAngle,
                    false
            );

            telemetry.addData("Auto aim", autoAim ? "ON" : "OFF");
            telemetry.addData("Pose", "%.1f, %.1f, %.1f deg",
                    pose.getX(), pose.getY(), Math.toDegrees(pose.getHeading()));
            telemetry.addData("Velocity", "%.1f, %.1f in/s",
                    velocity.getXComponent(), velocity.getYComponent());
            telemetry.addData("fw p", centerMotorPower);
////            telemetry.addData("Turret tag", turret.hasTarget() ? String.valueOf(turret.getLastTagId()) : "none");
////            telemetry.addData("Turret bearing", "%.1f deg", turret.getLastBearingDegrees());
////            telemetry.addData("Turret range", "%.1f in", turret.getLastRangeInches());
////            telemetry.addData("Lead", "%.1f deg", leadDeg);
////            telemetry.addData("Compensated", compensated ? "YES" : "NO");
////            telemetry.addData("Comp RPM", "%.0f rpm", compensatedRpm);
//            telemetry.addData("Flywheel target", "%.0f rpm", shooter.getTargetRpm());
//            telemetry.addData("Flywheel actual", "%.0f rpm", shooter.getVelocityRpm());
//            // Show the elapsed game time.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }

        shooter.stop();
//        turret.close();
    }

}