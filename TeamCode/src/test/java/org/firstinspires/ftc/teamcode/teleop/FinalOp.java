// Wifi password is "password"

package org.firstinspires.ftc.teamcode.teleop;


import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.oakslib.controllers.PIDFController;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp

public class FinalOp extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
//    private CRServo armServo = null;
//    private Servo clawServo = null;
    private double armServoPower = .5;

    private boolean targetFound = false;
    private double turnAngle = 0;

    private VisionPortal visionPortal; // Used to manage the video source.

    private AprilTagProcessor aprilTag;              // Used for managing the AprilTag detection process.
    final double DESIRED_DISTANCE = 24.0;
    final double SPEED_GAIN =   0.02 ;   //  Speed Control "Gain". e.g. Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
    final double TURN_GAIN  =   0.01 ;   //  Turn Control "Gain".  e.g. Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)

    final double MAX_AUTO_SPEED = 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_TURN  = 0.25;  //  Clip the turn speed to this max value (adjust for your robot)
    private final int DESIRED_TAG_ID = 20;

    AprilTagDetection desiredTag = null;

    @Override
    public void runOpMode() {
        Motor lf = new Motor(hardwareMap, "frontLeftMotor", Motor.GoBILDA.RPM_312);
        Motor rf = new Motor(hardwareMap, "frontRightMotor", Motor.GoBILDA.RPM_312);
        Motor lb = new Motor(hardwareMap, "backLeftMotor", Motor.GoBILDA.RPM_312);
        Motor rb = new Motor(hardwareMap, "backRightMotor", Motor.GoBILDA.RPM_312);
        ServoEx armServo = new SimpleServo(hardwareMap, "armServo", 0,180);

        PIDFController flyWheelController = new PIDFController(0.01, 0, 0.001, 0.05);

        armServo.setPosition(.51);

        MecanumDrive drive = new MecanumDrive(
                lf,
                rf,
                lb,
                rb
        );
        drive.setRightSideInverted(true);
        DcMotorEx centerMotor = hardwareMap.get(DcMotorEx.class, "centerMotor");
        centerMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        IMU imu = hardwareMap.get(IMU.class, "imu");
//        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(LogoFacingDirection.LEFT)));
        initAprilTag();
        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        double centerMotorPower = 0;

        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Set Fly Wheel Motor Power when Right Bumper is Pressed
            if (gamepad1.rightBumperWasPressed()) {
                if (centerMotorPower == -1) {
                    centerMotorPower = 0;
                }else{
                    centerMotorPower = -1;
                }
            }

            if (gamepad1.dpad_up) {
                armServo.setPosition(.601);

            } else {
                armServo.setPosition(.51);
            }

            // Calculate heading

            targetFound = false;
            desiredTag = null;

            // Step through the list of detected tags and look for a matching tag
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            for (AprilTagDetection detection : currentDetections) {
                // Look to see if we have size info on this tag.
                if (detection.metadata != null) {
                    //  Check to see if we want to track towards this tag.
                    if (detection.id == DESIRED_TAG_ID) {
                        // Yes, we want to use this tag.
                        targetFound = true;
                        desiredTag = detection;
                        break;
                    } else {
                        // This tag is in the library, but we do not want to track it right now.
                        telemetry.addData("Skipping", "Tag ID %d is not desired", detection.id);
                    }
                } else {
                    // This tag is NOT in the library, so we don't have enough information to track to it.
                    telemetry.addData("Unknown", "Tag ID %d is not in TagLibrary", detection.id);
                }
            }

//            double  rangeError   = (desiredTag.ftcPose.range - DESIRED_DISTANCE);
            if (targetFound) {
                double headingError = desiredTag.ftcPose.bearing;
                turnAngle = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
            }
            else {
                turnAngle = 0;
            }
            // Use the speed and turn "gains" to calculate how we want the robot to move.  Clip it to the maximum
//            driveSpeed = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);

            drive.driveFieldCentric(
                    gamepad1.left_stick_x,
                    gamepad1.left_stick_y,
                    turnAngle,
                    0
            );

            centerMotor.setPower((flyWheelController.calculate(centerMotorPower*6000, (centerMotor.getVelocity(AngleUnit.DEGREES)/360)))/6000);
            // Show the elapsed game time.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }

    }

    private void initAprilTag() {
        // Create the AprilTag processor by using a builder.
        aprilTag = new AprilTagProcessor.Builder().build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // e.g. Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag.setDecimation(3);

        // Create the vision portal by using a builder.
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .build();
    }}