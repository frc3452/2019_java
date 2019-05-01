package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.ConfigurableDrive.Rotation2d;

public class Left_Rocket_Far_Same extends PathContainer {
    public Left_Rocket_Far_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(127, 205, 15, 30));
        sWaypoints.add(new Waypoint(250, 258, 15, 90));
        sWaypoints.add(new Waypoint(289, 286, 0, 90));
    }

    @Override
    public boolean isReversed() {
        return false;
    }

    @Override
    public Rotation2d getEndGyroMovement() {
        return Rotation2d.fromDegrees(270 - 61.25);
    }
}