// Wifi password is "password"

package org.firstinspires.ftc.teamcode.teleop;


import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.RevIMU;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
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
        Motor centerMotor = new Motor(hardwareMap, "centerMotor", Motor.GoBILDA.RPM_312);
//        Motor leftLiftMotor = new Motor(hardwareMap, "leftLiftMotor", Motor.GoBILDA.RPM_1150);
//        Motor rightLiftMotor = new Motor(hardwareMap, "rightLiftMotor", Motor.GoBILDA.RPM_1150);

//        centerMotor.setRunMode(Motor.RunMode.);
//        leftLiftMotor.setRunMode(Motor.RunMode.RawPower);
//        rightLiftMotor.setRunMode(Motor.RunMode.RawPower);

        RevIMU imu = new RevIMU(hardwareMap);
        imu.init();
        GamepadEx driverOp = new GamepadEx(gamepad1);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        double centerMotorPower = 0;

        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            initAprilTag();

            // Set Arms Motor Power if right bumper is down
            if (gamepad1.rightBumperWasPressed()) {
                if (centerMotorPower == -.65) {
                    centerMotorPower = 0;
                }else{
                    centerMotorPower = -.65;
                }
            }

            // Set Arms Motor Power if right bumper is down

//            if (gamepad1.a) {
//                centerMotorPower = -.5;
//            } else if (gamepad1.b) {
//                centerMotorPower = .5;
//            }

            // Set Servo position if right bumper is down
//
            if (driverOp.isDown(GamepadKeys.Button.DPAD_UP) ) {
                armServo.setPosition(.601);
                sleep(150);
                armServo.setPosition(.51);
            }
//            } else if (driverOp.isDown(GamepadKeys.Button.DPAD_DOWN)) {
//                armServo.setPosition(.601);
//
//
//            }

            // Set Arms Motor Power if right bumper is down

//            if (gamepad1.dpad_left) {
//                clawServo.setPosition(.8);
//            } else if (gamepad1.dpad_right) {
//                clawServo.setPosition(1);
//            }

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
                    driverOp.getLeftX(),
                    driverOp.getLeftY(),
                    turnAngle,
                    0
            );

//            leftLiftMotor.set(liftMotorPower);
//            rightLiftMotor.set(liftMotorPower);
            centerMotor.set(flyWheelController.calculate(6000,centerMotor.getCurrentPosition()));
//            armServo.setPosition(armServoPower);
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