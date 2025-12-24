package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.oakslib.command.Command;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;

public class SetFlyWheel extends Command {

    private final double m_flyWheelPower;

    public SetFlyWheel(double power, ShooterSubsystem shooterSubsystem) {
        m_flyWheelPower = power;
    }
    public void execute() {

    }
}
