package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Left_Rocket_Mid_Opp extends PathContainer {
    public Left_Rocket_Mid_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(153, 205, 15, 30));
        sWaypoints.add(new Waypoint(204, 120, 25, 60));
        sWaypoints.add(new Waypoint(229.28, 80.32300000000001, 15, 60)/* .setFieldAdaption(- Rocket -) */);
        sWaypoints.add(new Waypoint(229.28, 63.13150000000002, 1, 30)/* .setFieldAdaption(- Rocket -) */);
        sWaypoints.add(new Waypoint(229.28, 46.94, 0, 30)/* .setFieldAdaption(- Rocket -) */);
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}