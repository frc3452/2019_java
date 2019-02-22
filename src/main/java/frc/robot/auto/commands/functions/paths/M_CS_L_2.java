package frc.robot.auto.commands.functions.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class M_CS_L_2 extends PathContainer {
    public M_CS_L_2() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(201.75, 172.88, 0, 30, "undefined"));
        sWaypoints.add(new Waypoint(163.999809272005, 172.88, 15, 60, "undefined"));
        sWaypoints.add(new Waypoint(173, 231, 15, 60));
        sWaypoints.add(new Waypoint(201, 199, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}