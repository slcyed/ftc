package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class BaseRobot {
    public DcMotor leftFrontDrive = null;
    public DcMotor leftBackDrive = null;
    public DcMotor rightFrontDrive = null;
    public DcMotor rightBackDrive = null;
    public DcMotor centerMotor = null;
    public DcMotor leftLiftMotor = null;
    public DcMotor rightLiftMotor = null;
    public CRServo armServo = null;
    public Servo clawServo = null;
    public double armServoPower = .5;

    /**
     * @param a
     * @param b
     */

    public void setup(LinearOpMode OpMode) {
        //Expansion hub 1
        leftFrontDrive  = OpMode.hardwareMap.get(DcMotor.class, "frontLeftMotor");
        //Expansion hub 0
        leftBackDrive  = OpMode.hardwareMap.get(DcMotor.class, "backLeftMotor");
        //Control hub 2
        rightFrontDrive = OpMode.hardwareMap.get(DcMotor.class, "frontRightMotor");
        //Control hub 1
        rightBackDrive = OpMode.hardwareMap.get(DcMotor.class, "backRightMotor");
        //Expansion hub 2
        leftLiftMotor = OpMode.hardwareMap.get(DcMotor.class, "leftLiftMotor");
        //Control hub 0
        rightLiftMotor = OpMode.hardwareMap.get(DcMotor.class, "rightLiftMotor");
        //Control hub 3
        centerMotor = OpMode.hardwareMap.get(DcMotor.class, "centerMotor");
        //Servo Port 0
        armServo = OpMode.hardwareMap.get(CRServo.class, "armServo");
        //Servo Port 1
        clawServo = OpMode.hardwareMap.get(Servo.class, "clawServo");

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        leftLiftMotor.setDirection(DcMotor.Direction.REVERSE);
        rightLiftMotor.setDirection(DcMotor.Direction.REVERSE);
        centerMotor.setDirection(DcMotor.Direction.REVERSE);
    }
}
