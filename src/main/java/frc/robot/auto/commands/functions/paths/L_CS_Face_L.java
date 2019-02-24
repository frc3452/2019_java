package frc.robot.auto.commands.functions.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class L_CS_Face_L extends PathContainer {
    public L_CS_Face_L() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(139, 205, 15, 50));
        sWaypoints.add(new Waypoint(166.092, 172.12, 15, 60));
        sWaypoints.add(new Waypoint(182.75, 172.12, 1, 30));
        sWaypoints.add(new Waypoint(200.75, 172.12, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}