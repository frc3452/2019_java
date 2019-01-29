package frc.robot.commands.paths;
import java.util.ArrayList;

import frc.robot.commands.drive.pathfollowing.PathBuilder;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.control.Path;

public class Straight_Curve_Left extends PathContainer {

    @Override
    public Path buildPath() {
        this.sWaypoints = new ArrayList<Waypoint>();
      
        final double speed = 80;

        sWaypoints.add(new Waypoint(20, 48, 0, 0));
        sWaypoints.add(new Waypoint(120, 48, 0, 80));
        sWaypoints.add(new Waypoint(232, 43, 40, 80));
        sWaypoints.add(new Waypoint(232, 103, 1, 80));
        sWaypoints.add(new Waypoint(232, 190, 0, 80));

        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }
    
    @Override
    public boolean isReversed() {
        return false;
    }
}