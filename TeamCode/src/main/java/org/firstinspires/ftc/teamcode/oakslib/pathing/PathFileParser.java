package org.firstinspires.ftc.teamcode.oakslib.pathing;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class PathFileParser {
    public static PathChain parsePathFile(Follower follower, File file) throws FileNotFoundException {
        JsonParser parser = new JsonParser();

        JsonObject root = parser.parse(new JsonReader(new FileReader(file))).getAsJsonObject();

        PathBuilder pathBuilder = follower.pathBuilder();

        double goalRotation = root.getAsJsonObject("goalEndState").get("rotation").getAsDouble();
        double startRotation = root.getAsJsonObject("idealStartingState").get("rotation").getAsDouble();

        JsonArray waypoints = root.getAsJsonArray("waypoints");

        for (int i=0; i < (waypoints.size() - 1); i++){
            JsonObject anchor = waypoints.get(i).getAsJsonObject().getAsJsonObject("anchor");
            double ax = anchor.get("x").getAsDouble();
            double ay = anchor.get("y").getAsDouble();

            JsonObject nextAnchor = waypoints.get(i + 1).getAsJsonObject().getAsJsonObject("anchor");
            double nax = nextAnchor.get("x").getAsDouble();
            double nay = nextAnchor.get("y").getAsDouble();


            JsonObject next = waypoints.get(i).getAsJsonObject().getAsJsonObject("nextControl");
            double nx = next.get("x").getAsDouble();
            double ny = next.get("y").getAsDouble();

            ArrayList<Pose> poses = new ArrayList<Pose>();

            poses.add(new Pose(ax,ay));

            poses.add(new Pose(nx, ny));

            poses.add(new Pose(nax,nay));

            pathBuilder.addPath(new BezierCurve(poses));



        }
        pathBuilder.setLinearHeadingInterpolation(Math.toRadians(startRotation), Math.toRadians(goalRotation));

        return pathBuilder.build();
    }
}
