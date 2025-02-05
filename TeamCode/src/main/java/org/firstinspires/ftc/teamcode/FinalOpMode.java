// Wifi password is "password"

package org.firstinspires.ftc.teamcode;


import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.hardware.RevIMU;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp

public class FinalOpMode extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private com.qualcomm.robotcore.hardware.CRServo armServo = null;
    private Servo clawServo = null;
    private double armServoPower = .5;

    @Override
    public void runOpMode() {
        Motor lf = new Motor(hardwareMap, "frontLeftMotor", Motor.GoBILDA.RPM_1150);
        Motor rf = new Motor(hardwareMap, "frontRightMotor", Motor.GoBILDA.RPM_1150);
        Motor lb = new Motor(hardwareMap, "backLeftMotor", Motor.GoBILDA.RPM_1150);
        Motor rb = new Motor(hardwareMap, "backRightMotor", Motor.GoBILDA.RPM_1150);

        MecanumDrive drive = new MecanumDrive(
                lf,
                rf,
                lb,
                rb
        );
        drive.setRightSideInverted(true);
        Motor centerMotor = new Motor(hardwareMap, "centerMotor", Motor.GoBILDA.RPM_1150);
        Motor leftLiftMotor = new Motor(hardwareMap, "leftLiftMotor", Motor.GoBILDA.RPM_1150);
        Motor rightLiftMotor = new Motor(hardwareMap, "rightLiftMotor", Motor.GoBILDA.RPM_1150);


        centerMotor.setRunMode(Motor.RunMode.RawPower);
        leftLiftMotor.setRunMode(Motor.RunMode.RawPower);
        rightLiftMotor.setRunMode(Motor.RunMode.RawPower);

        RevIMU imu = new RevIMU(hardwareMap);
        imu.init();
        GamepadEx driverOp = new GamepadEx(gamepad1);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            double liftMotorPower = 0;
            double centerMotorPower = 0;

            // Set Arms Motor Power if right bumper is down
            if (gamepad1.right_bumper) {
                liftMotorPower = -1;
//                leftLiftMotor.setPower(leftLiftMotorPower);
//                rightLiftMotor.setPower(rightLiftMotorPower);
//                sleep(3600);
//                leftLiftMotorPower = 0;
//                rightLiftMotorPower = 0;
//                leftLiftMotor.setPower(leftLiftMotorPower);
//                rightLiftMotor.setPower(rightLiftMotorPower);

            } else if (gamepad1.right_trigger>0.0) {
                liftMotorPower = 1;

//                sleep(3500);
//                leftLiftMotorPower = 0;
//                rightLiftMotorPower = 0;
//                leftLiftMotor.setPower(leftLiftMotorPower);
//                rightLiftMotor.setPower(rightLiftMotorPower);

            }

            // Set Arms Motor Power if right bumper is down

            if (gamepad1.a) {
                centerMotorPower = -.5;
            } else if (gamepad1.b) {
                centerMotorPower = .5;
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

            drive.driveRobotCentric(
                    driverOp.getLeftX(),
                    driverOp.getLeftY(),
                    driverOp.getRightX(),
                    false
            );

            leftLiftMotor.set(liftMotorPower);
            rightLiftMotor.set(liftMotorPower);
            centerMotor.set(centerMotorPower);
            armServo.setPower(armServoPower);
            // Show the elapsed game time.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }}
