package frc.robot.auto.commands.functions.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class M_CS_L_4 extends PathContainer {
    public M_CS_L_4() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(18.5, 298.28, 0, 30));
        sWaypoints.add(new Waypoint(57.02127204545561, 298.28, 15, 60));
        sWaypoints.add(new Waypoint(282.5, 222, 15, 100));
        sWaypoints.add(new Waypoint(282.5, 277.0166325843349, 0, 45));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}