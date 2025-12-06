package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public abstract class CommandOpMode extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();

        waitForStart();

        while(opModeIsActive()){
            run();
        }
    }

    public abstract void initialize();

    public void run() {

    }
}
