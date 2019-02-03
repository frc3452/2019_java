package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class CS_2 extends PathContainer {
    public CS_2() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(203, 173, 0, 0));
        sWaypoints.add(new Waypoint(172, 173, 10, 45));
        sWaypoints.add(new Waypoint(179, 209, 10, 45));
        sWaypoints.add(new Waypoint(204, 194, 0, 45));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}