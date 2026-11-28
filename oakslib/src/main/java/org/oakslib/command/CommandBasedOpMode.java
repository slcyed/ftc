package org.oakslib.command;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public abstract class CommandBasedOpMode extends OpMode {

    public void schedule(Command... commands) {
        CommandScheduler.getInstance().schedule(commands);
    }

    public void register(Subsystem... subsystems) {
        CommandScheduler.getInstance().registerSubsystem(subsystems);
    }

    @Override
    public void init() {
        CommandScheduler scheduler = CommandScheduler.getInstance();
        scheduler.reset();
        scheduler.enable();
        initialize();
    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void stop() {
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().disable();
    }

    /**
     * Callback invoked after the {@link CommandScheduler} has been reset and enabled for this
     * OpMode. Override to build Subsystems, set default commands, and bind Triggers.
     */
    protected void initialize() {}
}