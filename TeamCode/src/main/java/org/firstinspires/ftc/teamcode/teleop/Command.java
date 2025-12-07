package org.firstinspires.ftc.teamcode.teleop;

import java.util.Set;

public interface Command {
    public default void initialize() {};

    public default void execute() {};

    public default boolean isFinished() {
        return(false);
    }

    public default Set<Subsystem> getRequirements() {
        return java.util.Collections.emptySet();
    };

    public default void end() {};

    public default void interrupted() {
        end();
    }
}
