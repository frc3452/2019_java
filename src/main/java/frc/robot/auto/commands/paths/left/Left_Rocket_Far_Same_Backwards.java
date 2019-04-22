package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Left_Rocket_Far_Same_Backwards extends PathContainer {
    public Left_Rocket_Far_Same_Backwards() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(121, 205, 10, 20));
        sWaypoints.add(new Waypoint(171, 211, 30, 30));
        sWaypoints.add(new Waypoint(234, 253, 15, 90));
        sWaypoints.add(new Waypoint(280, 288, 0, 90));
        // System.out.println("LEFT ROCKET FAR SAME BACKWARDS\t" + getStartPose());
    }

    @Override
    public boolean isReversed() {
        return true;
    }

    @Override
    public Rotation2d getEndGyroMovement() {
        return Rotation2d.fromDegrees(270 - 61.25);
    }
}