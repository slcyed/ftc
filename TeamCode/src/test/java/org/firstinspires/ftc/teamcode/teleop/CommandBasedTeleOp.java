package org.firstinspires.ftc.teamcode.teleop;

import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.follower;

import android.content.Context;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.commands.SetFlyWheel;
import org.firstinspires.ftc.teamcode.oakslib.command.CommandBasedOpMode;
import org.firstinspires.ftc.teamcode.oakslib.command.CommandScheduler;
import org.firstinspires.ftc.teamcode.oakslib.command.Trigger;
import org.firstinspires.ftc.teamcode.oakslib.pathing.PathFileParser;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class CommandBasedTeleOp extends CommandBasedOpMode {

    @Test
    public void init() {
//        System.out.println(context.getResources().openRawResource(R.raw.path1));
//        try {
//            InputStream in = hardwareMap.appContext.getAssets().open("path1.path");
//            PathChain path = PathFileParser.parsePathFile(follower,in);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        ShooterSubsystem testSubsystem = new ShooterSubsystem();
//        int x = 0;
        Trigger rightBumper = new Trigger(() -> true);
        rightBumper.whileTrue(new SetFlyWheel("su",testSubsystem));

//        CommandScheduler.getInstance().schedule(new SetFlyWheel("no",testSubsystem));
        for (int i = 0; i < 100; i++) {
            CommandScheduler.getInstance().run();
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
