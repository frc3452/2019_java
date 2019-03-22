package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Rocket_Mid_Turn_Around_Same extends PathContainer {
    public Rocket_Mid_Turn_Around_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(229.28, 277.06, 0, 30).setFieldAdaption(PathAdapter.rocketMid));
        sWaypoints.add(new Waypoint(229.28, 257, 15, 30).setFieldAdaption(PathAdapter.rocketMid));
        sWaypoints.add(new Waypoint(264, 246, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}