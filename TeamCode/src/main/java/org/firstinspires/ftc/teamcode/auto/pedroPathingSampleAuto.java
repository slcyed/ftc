package org.firstinspires.ftc.teamcode.auto;

import static org.firstinspires.ftc.teamcode.oakslib.pathing.PathFileParser.parsePathFile;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.follower;

import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.oakslib.pathing.PathFileParser;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.io.File;
import java.io.FileNotFoundException;

@Autonomous
public class pedroPathingSampleAuto extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
//        File file = new File("");
//        PathChain path = null;
//        if (file != null) {
//            path = parsePathFile(follower, file);
//        }
//        waitForStart();
//
//        follower.followPath(path);
    }
}
