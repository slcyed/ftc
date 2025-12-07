package org.firstinspires.ftc.teamcode.teleop;

public class RunCommand implements Command {
    public Runnable m_runnable;
    public RunCommand(Runnable runnable) {
        m_runnable = runnable;
    }

    
}
