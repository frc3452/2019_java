package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Cargo_Ship_2 extends PathContainer {
    public Cargo_Ship_2() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(262, 204, 0, 0));
        sWaypoints.add(new Waypoint(262, 226, 15, 30));
        sWaypoints.add(new Waypoint(227, 250, 15, 30));
        sWaypoints.add(new Waypoint(249, 267, 0, 30));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}