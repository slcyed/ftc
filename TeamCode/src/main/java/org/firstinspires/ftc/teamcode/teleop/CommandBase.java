package org.firstinspires.ftc.teamcode.teleop;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandBase {
    protected String m_name = this.getClass().getSimpleName();
    protected String m_subsystem = "Ungrouped";
    protected Set<Subsystem> m_requirements = new HashSet<>();

    /**
     * Adds the specified requirements to the command.
     *
     * @param requirements the requirements to add
     */
    public final void addRequirements(Subsystem... requirements) {
        m_requirements.addAll(Arrays.asList(requirements));
    }

    public Set<Subsystem> getRequirements() {
        return m_requirements;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getSubsystem() {
        return m_subsystem;
    }

    public void setSubsystem(String subsystem) {
        m_subsystem = subsystem;
    }
}
