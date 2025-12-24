package org.firstinspires.ftc.teamcode.oakslib.command;

public class SubsystemBase implements Subsystem {
    /** Constructor. Telemetry/log name defaults to the classname. */
    @SuppressWarnings("this-escape")
    public SubsystemBase() {
        String name = this.getClass().getSimpleName();
        name = name.substring(name.lastIndexOf('.') + 1);
        CommandScheduler.getInstance().registerSubsystem(this);
    }
}
