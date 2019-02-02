package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Left_To_Rocket_L extends PathContainer {
    public Left_To_Rocket_L() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67, 205, 0, 0));
        sWaypoints.add(new Waypoint(128, 205, 15, 30));
        sWaypoints.add(new Waypoint(153, 273, 15, 50));
        sWaypoints.add(new Waypoint(195.5, 295.5, 0, 50));
    }

    @Override
    public boolean isReversed() {
        return false;
    }

}