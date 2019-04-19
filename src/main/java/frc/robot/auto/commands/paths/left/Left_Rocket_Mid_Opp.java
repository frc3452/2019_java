package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Left_Rocket_Mid_Opp extends PathContainer {
    public Left_Rocket_Mid_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(130, 205, 15, 30));
        sWaypoints.add(new Waypoint(204, 120, 25, 60));
        sWaypoints.add(new Waypoint(229.28, 80.32300000000001, 15, 60).setFieldAdaption(PathAdapter.rocketMid));
        sWaypoints.add(new Waypoint(229.28, 63.13150000000002, 1, 30).setFieldAdaption(PathAdapter.rocketMid));
        sWaypoints.add(new Waypoint(229.28, 46.94, 0, 30).setFieldAdaption(PathAdapter.rocketMid));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}