package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Left_Rocket_Mid_Same extends PathContainer {
    public Left_Rocket_Mid_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(163, 207, 15, 60));
        sWaypoints.add(new Waypoint(204, 204, 25, 60));
        sWaypoints.add(new Waypoint(229.28, 243.677, 15, 60)/*.setFieldAdaption(- Rocket -)*/);
        sWaypoints.add(new Waypoint(229.28, 260.8685, 1, 30)/*.setFieldAdaption(- Rocket -)*/);
        sWaypoints.add(new Waypoint(229.28, 277.06, 0, 30)/*.setFieldAdaption(- Rocket -)*/);
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}