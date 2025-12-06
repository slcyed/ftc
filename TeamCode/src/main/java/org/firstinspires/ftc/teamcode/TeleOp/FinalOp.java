// Wifi password is "password"

package org.firstinspires.ftc.teamcode.TeleOp;


import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.RevIMU;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp

public class FinalOp extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
//    private CRServo armServo = null;
//    private Servo clawServo = null;
    private double armServoPower = .5;

    @Override
    public void runOpMode() {
        Motor lf = new Motor(hardwareMap, "frontLeftMotor", Motor.GoBILDA.RPM_1150);
        Motor rf = new Motor(hardwareMap, "frontRightMotor", Motor.GoBILDA.RPM_1150);
        Motor lb = new Motor(hardwareMap, "backLeftMotor", Motor.GoBILDA.RPM_1150);
        Motor rb = new Motor(hardwareMap, "backRightMotor", Motor.GoBILDA.RPM_1150);
        ServoEx armServo = new SimpleServo(hardwareMap, "armServo", 0,180);

        armServo.setPosition(.51);

        MecanumDrive drive = new MecanumDrive(
                lf,
                rf,
                lb,
                rb
        );
        drive.setRightSideInverted(true);
        Motor centerMotor = new Motor(hardwareMap, "centerMotor", Motor.GoBILDA.RPM_1150);
//        Motor leftLiftMotor = new Motor(hardwareMap, "leftLiftMotor", Motor.GoBILDA.RPM_1150);
//        Motor rightLiftMotor = new Motor(hardwareMap, "rightLiftMotor", Motor.GoBILDA.RPM_1150);


        centerMotor.setRunMode(Motor.RunMode.RawPower);
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


            // Set Arms Motor Power if right bumper is down
            if (gamepad1.rightBumperWasPressed()) {
                if (centerMotorPower == -.75) {
                    centerMotorPower = 0;
                }else{
                    centerMotorPower = -.75;
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
//
            drive.driveRobotCentric(
                    driverOp.getLeftX(),
                    driverOp.getLeftY(),
                    driverOp.getRightX(),
                    false
            );

//            leftLiftMotor.set(liftMotorPower);
//            rightLiftMotor.set(liftMotorPower);
            centerMotor.set(centerMotorPower);
//            armServo.setPosition(armServoPower);
            // Show the elapsed game time.
            telemetry.addData("Status", "Run Time: " + runtime.toString() + armServo.getPosition());
            telemetry.update();
        }
    }}