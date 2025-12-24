package org.firstinspires.ftc.teamcode.diagnostics;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.oakslib.controllers.PIDFController;

@TeleOp(group = "Diagnostics")
public class FlyWheelTuning extends OpMode {

    public DcMotorEx flywheelMotor;

    public double highVelocity = 1500;

    public double lowVelocity = 900;

    double curTargetVelocity = highVelocity;

    double F = 0;

    double P = 0;

    double[] stepSizes = {10.0,1.0,0.1,0.001,0.0001};

    int stepIndex = 1;

    PIDFController pidfController = new PIDFController(P,0,0,F);

    @Override
    public void init() {
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "centerMotor");
        flywheelMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheelMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        telemetry.addLine("Init Complete");
    }

    @Override
    public void loop() {
        if (gamepad1.yWasPressed()) {
            if (curTargetVelocity == highVelocity) {
                curTargetVelocity = lowVelocity;
            } else {
                curTargetVelocity = highVelocity;
            }
        }

        if (gamepad1.bWasPressed()) {
            stepIndex = (stepIndex+1) % stepSizes.length;
        }

        if (gamepad1.dpadLeftWasPressed()) {
            F += stepSizes[stepIndex];
        }

        if (gamepad1.dpadUpWasPressed()) {
            F -= stepSizes[stepIndex];
        }

        pidfController.setkF(F);

        flywheelMotor.setPower((pidfController.calculate(curTargetVelocity, (flywheelMotor.getVelocity(AngleUnit.DEGREES))/360))/6000);

    }
}
