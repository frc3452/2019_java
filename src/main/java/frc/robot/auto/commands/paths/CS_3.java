package frc.robot.auto.commands.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;
import frc.robot.auto.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class CS_3 extends PathContainer {
    public CS_3() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(214, 214, 0, 0));
        sWaypoints.add(new Waypoint(179, 229, 20, 60));
        sWaypoints.add(new Waypoint(122, 298, 20, 60));
        sWaypoints.add(new Waypoint(49, 298, 1, 60));
        sWaypoints.add(PathAdapter.getFeederStation(new Waypoint(16, 298, 0, 30), true));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}