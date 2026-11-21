package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.oakslib.command.Command;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;

public class SetFlyWheel extends Command {
    private final ShooterSubsystem m_shooterSubsystem;

    private final Telemetry m_telemetry;

    private boolean m_tick = false;

    public SetFlyWheel(ShooterSubsystem shooterSubsystem, Telemetry telemetry) {
        m_shooterSubsystem = shooterSubsystem;
        m_telemetry = telemetry;
        addRequirements(m_shooterSubsystem);
    }

    @Override
    public void initialize() {
        if (!m_tick) {
            m_shooterSubsystem.updateFlyWheel(0.6);
            m_tick = true;
        } else {
            m_shooterSubsystem.updateFlyWheel(0);
            m_tick = false;
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
