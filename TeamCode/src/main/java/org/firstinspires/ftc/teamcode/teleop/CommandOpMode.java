package org.firstinspires.ftc.teamcode.teleop;

import org.firstinspires.ftc.teamcode.teleop.CommandScheduler;

import com.arcrobotics.ftclib.command.RunCommand;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public abstract class CommandOpMode extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();

        waitForStart();

        while(opModeIsActive()){
            CommandScheduler.getInstance().run();
        }
    }

    public abstract void initialize();

    public void schedule(Command command) {
        CommandScheduler.schedule(command);
    }

}
