package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.oakslib.pathing.PathFileParser;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.io.File;
import java.io.FileNotFoundException;

public class pedroPathingSampleAuto extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        try {
            PathChain path = PathFileParser.parsePathFile(Constants.createFollower(hardwareMap),new File("../../../../../../../../../src/main/deploy/pathplanner/paths/path1.path"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        waitForStart();

    }
}
