package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class CS_2 extends PathContainer {
    public CS_2() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(203, 173, 0, 0));
        sWaypoints.add(new Waypoint(172, 173, 15, 70));
        sWaypoints.add(new Waypoint(128, 211, 40, 70));
        sWaypoints.add(new Waypoint(172, 263, 25, 70));
        sWaypoints.add(new Waypoint(216, 263, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}