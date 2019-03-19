package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Left_Rocket_Far_Opp extends PathContainer {
    public Left_Rocket_Far_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(157, 205, 15, 30));
        sWaypoints.add(new Waypoint(185, 103, 25, 60));
        sWaypoints.add(new Waypoint(245, 98, 15, 60));
        sWaypoints.add(new Waypoint(288, 78, 15, 60));
        sWaypoints.add(new Waypoint(298, 45, 15, 60)/*.setFieldAdaption(- Rocket -)*/);
        sWaypoints.add(new Waypoint(278, 36, 0.5, 30))/*.setFieldAdaption(- Rocket -)*/;
        sWaypoints.add(new Waypoint(260.79, 27.04000000000002, 0, 30)/*.setFieldAdaption(- Rocket -)*/);
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}