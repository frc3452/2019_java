package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;

public class Left_Rocket_Mid_Same extends PathContainer {
    public Left_Rocket_Mid_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(204, 205, 15, 60));
        sWaypoints.add(new Waypoint(229.28, 223.677, 15, 60));
        sWaypoints.add(new Waypoint(229.28, 250.86849999999998, 1, 60));
        sWaypoints.add(new Waypoint(229.28, 277.06, 0, 30));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}