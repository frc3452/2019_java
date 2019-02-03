package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class CS_4 extends PathContainer {
    public CS_4() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(22, 298, 0, 0));
        sWaypoints.add(new Waypoint(63, 298, 15, 60));
        sWaypoints.add(new Waypoint(120, 286, 20, 60));
        sWaypoints.add(new Waypoint(155, 228, 25, 60));
        sWaypoints.add(new Waypoint(283, 228, 20, 60));
        sWaypoints.add(new Waypoint(283, 277, 0, 50));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}