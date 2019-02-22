package frc.robot.auto.commands.functions.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class M_CS_L_3 extends PathContainer {
    public M_CS_L_3() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(201, 199, 0, 60));
        sWaypoints.add(new Waypoint(173, 231, 15, 60));
        sWaypoints.add(new Waypoint(132, 299, 15, 60));
        sWaypoints.add(new Waypoint(57.02127204545561, 298.28, 0, 60));
        sWaypoints.add(new Waypoint(18.5, 298.28, 0, 30));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}