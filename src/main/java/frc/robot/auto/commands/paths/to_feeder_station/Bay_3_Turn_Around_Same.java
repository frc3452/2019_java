package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.pathadapter.PathAdapter;
import frc.robot.poofs.util.math.Rotation2d;

public class Bay_3_Turn_Around_Same extends PathContainer {
    public Bay_3_Turn_Around_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(304.25, 225.37, 0, 0));
        sWaypoints.add(new Waypoint(304.25, 249, 15, 60));
        sWaypoints.add(new Waypoint(95.001, 298.28, 15, 70));
        sWaypoints.add(new Waypoint(19.5, 298.28, 0, 90).setFieldAdaption(PathAdapter.feederStation));
    }

    @Override
    public boolean isReversed() {
        return false;
    }

    @Override
    public Rotation2d getStartGyroMovement() {
        return Rotation2d.fromDegrees(270);
    }
}