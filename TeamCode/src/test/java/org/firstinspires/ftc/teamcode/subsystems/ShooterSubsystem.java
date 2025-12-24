package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.oakslib.command.SubsystemBase;
import org.firstinspires.ftc.teamcode.oakslib.controllers.PIDFController;

public class ShooterSubsystem extends SubsystemBase {

//    private final DcMotorEx m_flyWheel;
//    private final PIDFController m_flyWheelController = new PIDFController(0.01, 0, 0.001, 0.05);;

    public ShooterSubsystem() {
    }

    public void updateFlyWheel(String power) {
        System.out.println(power);
    }
}
