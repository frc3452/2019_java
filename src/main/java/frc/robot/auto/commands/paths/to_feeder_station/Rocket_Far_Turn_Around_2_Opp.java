package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Rocket_Far_Turn_Around_2_Opp extends PathContainer {
    public Rocket_Far_Turn_Around_2_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(295, 234, 0, 60));
        sWaypoints.add(new Waypoint(287, 270, 15, 30));
        sWaypoints.add(new Waypoint(219, 259, 15, 60));
        sWaypoints.add(new Waypoint(161, 247, 15, 60));
        sWaypoints.add(new Waypoint(150, 214, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}