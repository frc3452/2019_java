package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Figure_8_3 extends PathContainer {
    public Figure_8_3() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67, 162, 0, 0));
        sWaypoints.add(new Waypoint(97, 162, 15, 30));
        sWaypoints.add(new Waypoint(125, 139, 15, 30));
        sWaypoints.add(new Waypoint(198, 162, 15, 30));
        sWaypoints.add(new Waypoint(229, 146, 15, 30));
        sWaypoints.add(new Waypoint(196, 131, 15, 30));
        sWaypoints.add(new Waypoint(132, 186, 15, 30));
        sWaypoints.add(new Waypoint(98, 175, 15, 30));
        sWaypoints.add(new Waypoint(111, 152, 15, 30));
        sWaypoints.add(new Waypoint(147, 144, 15, 30));
        sWaypoints.add(new Waypoint(199, 163, 15, 30));
        sWaypoints.add(new Waypoint(224, 146, 15, 30));
        sWaypoints.add(new Waypoint(206, 118, 15, 30));
        sWaypoints.add(new Waypoint(174, 129, 15, 30));
        sWaypoints.add(new Waypoint(154, 150, 15, 30));
        sWaypoints.add(new Waypoint(127, 182, 15, 30));
        sWaypoints.add(new Waypoint(120, 149, 15, 30));
        sWaypoints.add(new Waypoint(150, 144, 15, 30));
        sWaypoints.add(new Waypoint(196, 137, 15, 30));
        sWaypoints.add(new Waypoint(224, 121, 0, 30));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}