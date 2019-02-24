package frc.robot.auto.commands.functions.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Jank_To_Feed extends PathContainer {
    public Jank_To_Feed() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(211, 215, 0, 0));
        sWaypoints.add(new Waypoint(139.022, 298.28, 15, 70));
        sWaypoints.add(new Waypoint(59.5, 298.28, 1, 70));
        sWaypoints.add(new Waypoint(19.5, 298.28, 0, 70));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}