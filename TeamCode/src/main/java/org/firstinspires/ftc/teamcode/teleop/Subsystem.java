package org.firstinspires.ftc.teamcode.teleop;

public interface Subsystem {

    public default void periodic() {

    }

    public default void setDefaultCommand(Command command) {
        CommandScheduler.getInstance().setDefaultCommand(this,command);
    }

    public default Command getDefaultCommand() {
        return CommandScheduler.getInstance().getDefaultCommand(this);
    }
}
