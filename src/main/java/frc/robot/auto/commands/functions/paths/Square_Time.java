package frc.robot.auto.commands.functions.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Square_Time extends PathContainer {
    public Square_Time() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(78, 253, 0, 0));

        for (int i = 0; i < 20; i++) {
            sWaypoints.add(new Waypoint(198, 253, 25, 60));
            sWaypoints.add(new Waypoint(198, 100, 25, 60));
            sWaypoints.add(new Waypoint(125, 101, 20, 30));
            sWaypoints.add(new Waypoint(125, 179, 1, 30));
            sWaypoints.add(new Waypoint(125, 253, 25, 60));
        }
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}