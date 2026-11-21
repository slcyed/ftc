package org.firstinspires.ftc.teamcode.commands.starter;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.oakslib.command.Command;

public class ToggleIntake extends Command {
    private final IntakeSubsystem m_intakeSubsystem;

    private final Telemetry m_telemetry;

    private boolean m_tick = false;

    public ToggleIntake(IntakeSubsystem intakeSubsystem, Telemetry telemetry) {
        m_intakeSubsystem = intakeSubsystem;
        m_telemetry = telemetry;
        addRequirements(m_intakeSubsystem);
    }

    @Override
    public void initialize() {
        if (!m_tick) {
            m_intakeSubsystem.updateIntake(0.6);
            m_tick = true;
        } else {
            m_intakeSubsystem.updateIntake(0);
            m_tick = false;
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
