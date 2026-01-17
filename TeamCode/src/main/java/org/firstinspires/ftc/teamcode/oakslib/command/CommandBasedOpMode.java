package org.firstinspires.ftc.teamcode.oakslib.command;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public abstract class CommandBasedOpMode extends OpMode {

    public void schedule(Command... commands) {
        CommandScheduler.getInstance().schedule(commands);
    }

    public void register(Subsystem... subsystems) {
        CommandScheduler.getInstance().registerSubsystem(subsystems);
    }

    @Override
    public abstract void init();

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
    }
}
