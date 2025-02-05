// Wifi password is "password"

package org.firstinspires.ftc.teamcode;

//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.CRServo;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.util.ElapsedTime;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp

public class DriveOpMode extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;
    private DcMotor centerMotor = null;
    private DcMotor leftLiftMotor = null;
    private DcMotor rightLiftMotor = null;
    private CRServo armServo = null;
    private Servo clawServo = null;
    private double armServoPower = .5;


    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.

        //Expansion hub 1
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        //Expansion hub 0
        leftBackDrive  = hardwareMap.get(DcMotor.class, "backLeftMotor");
        //Control hub 2
        rightFrontDrive = hardwareMap.get(DcMotor.class, "frontRightMotor");
        //Control hub 1
        rightBackDrive = hardwareMap.get(DcMotor.class, "backRightMotor");
        //Expansion hub 2
        leftLiftMotor = hardwareMap.get(DcMotor.class, "leftLiftMotor");
        //Control hub 0
        rightLiftMotor = hardwareMap.get(DcMotor.class, "rightLiftMotor");
        //Control hub 3
        centerMotor = hardwareMap.get(DcMotor.class, "centerMotor");
        //Servo Port 0
        armServo = hardwareMap.get(CRServo.class, "armServo");
        //Servo Port 1
        clawServo = hardwareMap.get(Servo.class, "clawServo");


        // ########################################################################################
        // !!!            IMPORTANT Drive Information. Test your motor directions.            !!!!!
        // ########################################################################################
        // Most robots need the motors on one side to be reversed to drive forward.
        // The motor reversals shown here are for a "direct drive" robot (the wheels turn the same direction as the motor shaft)
        // If your robot has additional gear reductions or uses a right-angled drive, it's important to ensure
        // that your motors are turning in the correct direction.  So, start out with the reversals here, BUT
        // when you first test your robot, push the left joystick forward and observe the direction the wheels turn.
        // Reverse the direction (flip FORWARD <-> REVERSE ) of any wheel that runs backward
        // Keep testing until ALL the wheels move the robot forward when you push the left joystick forward.
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        leftLiftMotor.setDirection(DcMotor.Direction.REVERSE);
        rightLiftMotor.setDirection(DcMotor.Direction.REVERSE);
        centerMotor.setDirection(DcMotor.Direction.REVERSE);


        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  -gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double leftFrontPower  = -axial + lateral + yaw;
            double rightFrontPower = -axial - lateral - yaw;
            double leftBackPower   = -axial - lateral + yaw;
            double rightBackPower  = -axial + lateral - yaw;

            double rightLiftMotorPower = 0;
            double leftLiftMotorPower = 0;

            double centerMotorPower = 0;

            // Set Arms Motor Power if right bumper is down
            if (gamepad1.right_bumper) {
                leftLiftMotorPower = -1;
                rightLiftMotorPower = -1;
//                leftLiftMotor.setPower(leftLiftMotorPower);
//                rightLiftMotor.setPower(rightLiftMotorPower);
//                sleep(3600);
//                leftLiftMotorPower = 0;
//                rightLiftMotorPower = 0;
//                leftLiftMotor.setPower(leftLiftMotorPower);
//                rightLiftMotor.setPower(rightLiftMotorPower);

            } else if (gamepad1.right_trigger>0.0) {
                leftLiftMotorPower = 1;
                rightLiftMotorPower = 1;

//                sleep(3500);
//                leftLiftMotorPower = 0;
//                rightLiftMotorPower = 0;
//                leftLiftMotor.setPower(leftLiftMotorPower);
//                rightLiftMotor.setPower(rightLiftMotorPower);

            }

            // Set Arms Motor Power if right bumper is down

            if (gamepad1.a) {
                centerMotorPower = -1;
            } else if (gamepad1.b) {
                centerMotorPower = 1;
            }

            // Set Servo position if right bumper is down

            if (gamepad1.dpad_up) {
                armServoPower = 1;
            } else if (gamepad1.dpad_down) {
                armServoPower = -1;
            }

            // Set Arms Motor Power if right bumper is down

            if (gamepad1.dpad_left) {
                clawServo.setPosition(.8);
            } else if (gamepad1.dpad_right) {
                clawServo.setPosition(1);
            }

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));

            if (max > 1.0) {
                leftFrontPower  /= max;
                rightFrontPower /= max;
                leftBackPower   /= max;
                rightBackPower  /= max;
            }


            // Send calculated power to wheels++
            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightBackDrive.setPower(rightBackPower);
            centerMotor.setPower(centerMotorPower);
            leftLiftMotor.setPower(leftLiftMotorPower);
            rightLiftMotor.setPower(rightLiftMotorPower);
            armServo.setPower(armServoPower);



            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
            telemetry.update();


        }

    }}
