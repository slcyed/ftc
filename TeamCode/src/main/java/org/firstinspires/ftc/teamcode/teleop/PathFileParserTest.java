package org.firstinspires.ftc.teamcode.teleop;

import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.follower;

import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.teamcode.oakslib.command.CommandBasedOpMode;
import org.firstinspires.ftc.teamcode.oakslib.pathing.PathFileParser;

import java.io.IOException;
import java.io.InputStream;

public class PathFileParserTest extends CommandBasedOpMode {

    @Override
    public void init() {
//        System.out.println(context.getResources().openRawResource(R.raw.path1));
        try {
            InputStream in = hardwareMap.appContext.getAssets().open("path1.path");
            PathChain path = PathFileParser.parsePathFile(follower,in);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        ShooterSubsystem testSubsystem = new ShooterSubsystem(hardwareMap.get(DcMotorEx.class,"centerMotor"));
//        int x = 0
//        Trigger rightBumper = new Trigger(() -> x==0);
//        rightBumper.onTrue(new SetFlyWheel(1,flyWheelSubsystem));
    }
}

