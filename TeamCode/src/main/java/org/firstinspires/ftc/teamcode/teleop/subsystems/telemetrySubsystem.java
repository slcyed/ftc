package org.firstinspires.ftc.teamcode.teleop.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import org.firstinspires.ftc.teamcode.teleop.SubsystemBase;
import com.qualcomm.robotcore.util.ElapsedTime;

public class telemetrySubsystem extends SubsystemBase {
    public void updateTelemetry(ElapsedTime runtime) {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.update();
    }
}
