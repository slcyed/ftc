package org.firstinspires.ftc.teamcode.oakslib.command;

public interface Subsystem {

    /**
     * This method is called periodically by the {@link CommandScheduler}. Useful for updating
     * subsystem-specific state that you don't want to offload to a {@link Command}. Teams should try
     * to be consistent within their own codebases about which responsibilities will be handled by
     * Commands, and which will be handled here.
     */
    default void periodic() {}

    /**
     * Gets the subsystem name of this Subsystem.
     *
     * @return Subsystem name
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Sets the default {@link Command} of the subsystem. The default command will be automatically
     * scheduled when no other commands are scheduled that require the subsystem. Default commands
     * should generally not end on their own, i.e. their {@link Command#isFinished()} method should
     * always return false. Will automatically register this subsystem with the {@link
     * CommandScheduler}.
     *
     * @param defaultCommand the default command to associate with this subsystem
     */
    default void setDefaultCommand(Command defaultCommand) {
        CommandScheduler.getInstance().setDefaultCommand(this, defaultCommand);
    }

    /**
     * Removes the default command for the subsystem. This will not cancel the default command if it
     * is currently running.
     */
    default void removeDefaultCommand() {
        CommandScheduler.getInstance().removeDefaultCommand(this);
    }

    /**
     * Gets the default command for this subsystem. Returns null if no default command is currently
     * associated with the subsystem.
     *
     * @return the default command associated with this subsystem
     */
    default Command getDefaultCommand() {
        return CommandScheduler.getInstance().getDefaultCommand(this);
    }

    /**
     * Returns the command currently running on this subsystem. Returns null if no command is
     * currently scheduled that requires this subsystem.
     *
     * @return the scheduled command currently requiring this subsystem
     */
    default Command getCurrentCommand() {
        return CommandScheduler.getInstance().requiring(this);
    }

    /**
     * Registers this subsystem with the {@link CommandScheduler}, allowing its {@link
     * Subsystem#periodic()} method to be called when the scheduler runs.
     */
    default void register() {
        CommandScheduler.getInstance().registerSubsystem(this);
    }
}
