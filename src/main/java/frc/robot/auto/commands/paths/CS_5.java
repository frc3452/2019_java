package frc.robot.auto.commands.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;
import frc.robot.auto.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class CS_5 extends PathContainer {
    public CS_5() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(283, 277, 1, 0));
        sWaypoints.add(new Waypoint(283, 243, 1, 45));
        sWaypoints.add(PathAdapter.getHABBay(new Waypoint(283, 209, 1, 35), 2, true));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}