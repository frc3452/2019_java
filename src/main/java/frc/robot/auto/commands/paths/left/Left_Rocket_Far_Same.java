package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.pathadapter.PathAdapter;

public class Left_Rocket_Far_Same extends PathContainer {
    public Left_Rocket_Far_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(157, 205, 15, 30));
        sWaypoints.add(new Waypoint(245, 226, 15, 60));
        sWaypoints.add(new Waypoint(300, 277, 15, 60).setFieldAdaption(PathAdapter.rocketFar));
        sWaypoints.add(new Waypoint(278, 288, 0, 30).setFieldAdaption(PathAdapter.rocketFar));
        sWaypoints.add(new Waypoint(260.79, 296.96, 0, 30).setFieldAdaption(PathAdapter.rocketFar));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}