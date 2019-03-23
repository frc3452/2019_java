package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Left_Rocket_Close_Opp extends PathContainer {
    public Left_Rocket_Close_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(126, 205, 15, 30));
        sWaypoints.add(new Waypoint(153, 108, 15, 60));
        sWaypoints.add(new Waypoint(170.371, 41.907, 15, 60).setFieldAdaption(PathAdapter.rocketNear));
        sWaypoints.add(new Waypoint(183.92050432212503, 34.473507878199044, 1, 30).setFieldAdaption(PathAdapter.rocketNear));
        sWaypoints.add(new Waypoint(197.47, 27.04, 0, 30).setFieldAdaption(PathAdapter.rocketNear));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}