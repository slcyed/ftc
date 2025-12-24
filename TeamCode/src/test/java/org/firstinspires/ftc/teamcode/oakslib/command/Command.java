package org.firstinspires.ftc.teamcode.oakslib.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class Command {

    private final Set<Subsystem> m_requirements = new HashSet<>();

    protected Command() {
        String name = getClass().getName();
    }

    public void initialize() {}

    public void execute() {}

    public void end(boolean interrupted) {}

    public boolean isFinished() {
        return false;
    }

    public final void addRequirements(Subsystem... requirements) {
        m_requirements.addAll(Arrays.asList(requirements));
    }
    public Set<Subsystem> getRequirements() {
        return m_requirements;
    }

    public boolean isScheduled() {
        return CommandScheduler.getInstance().isScheduled(this);
    }

    public void cancel() {
        CommandScheduler.getInstance().cancel(this);
    }
}
