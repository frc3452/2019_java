package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;
import frc.robot.poofs.util.math.Rotation2d;

public class Rocket_Far_Turn_Around_Same extends PathContainer {
    public Rocket_Far_Turn_Around_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(235, 267, 0, 30));
        sWaypoints.add(new Waypoint(178, 257, 15, 60));
        sWaypoints.add(new Waypoint(120, 298.28, 15, 60));
        sWaypoints.add(new Waypoint(69.75, 298.28, 1, 30));
        sWaypoints.add(new Waypoint(19.5, 298.28, 0, 30).setFieldAdaption(PathAdapter.feederStation));
    }

    @Override
    public boolean isReversed() {
        return false;
    }

    @Override
    public Rotation2d getStartGyroMovement()
    {
        return Rotation2d.fromDegrees(160);
    }
}