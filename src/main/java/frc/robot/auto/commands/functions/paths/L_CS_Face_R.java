package frc.robot.auto.commands.functions.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class L_CS_Face_R extends PathContainer {
    public L_CS_Face_R() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(124, 205, 15, 50));
        sWaypoints.add(new Waypoint(124, 151.12, 15, 30));
        sWaypoints.add(new Waypoint(155.75, 151.12, 1, 30));
        sWaypoints.add(new Waypoint(199.99, 151.12, 0, 30));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}