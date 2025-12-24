package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.oakslib.command.Command;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;

public class SetFlyWheel extends Command {

    private final String m_flyWheelPower;
    private final ShooterSubsystem m_testSubsystem;

    public SetFlyWheel(String power, ShooterSubsystem shooterSubsystem) {
        m_flyWheelPower = power;
        m_testSubsystem = shooterSubsystem;
    }
    public void execute() {
        m_testSubsystem.updateFlyWheel(m_flyWheelPower);
    }
}
