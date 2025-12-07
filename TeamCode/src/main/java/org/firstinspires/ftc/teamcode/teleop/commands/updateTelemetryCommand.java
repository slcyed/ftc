package org.firstinspires.ftc.teamcode.teleop.commands;

import com.qualcomm.robotcore.robocol.Command;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.teleop.CommandBase;
import org.firstinspires.ftc.teamcode.teleop.subsystems.telemetrySubsystem;

public class updateTelemetryCommand extends CommandBase {

    private static telemetrySubsystem m_telemetrySubsystem;
    private static ElapsedTime m_runtime;
    public updateTelemetryCommand(telemetrySubsystem telemetrySystem, ElapsedTime runtime) {
        m_telemetrySubsystem = telemetrySystem;
        m_runtime = runtime;
    }

    public void execute() {
        m_telemetrySubsystem.updateTelemetry(m_runtime);
    }
}
