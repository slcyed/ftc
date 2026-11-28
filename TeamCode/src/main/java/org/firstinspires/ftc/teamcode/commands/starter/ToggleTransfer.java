package org.firstinspires.ftc.teamcode.commands.starter;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.oakslib.command.Command;

public class ToggleTransfer extends Command {
    private final IntakeSubsystem m_intakeSubsystem;

    private final Telemetry m_telemetry;

    private boolean m_tick = false;

    public ToggleTransfer(IntakeSubsystem intakeSubsystem, Telemetry telemetry) {
        m_intakeSubsystem = intakeSubsystem;
        m_telemetry = telemetry;
        addRequirements(m_intakeSubsystem);
    }

    @Override
    public void initialize() {
        if (!m_tick) {
            m_intakeSubsystem.transferOn();
            m_tick = true;
        } else {
            m_intakeSubsystem.transferOff();
            m_tick = false;
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
