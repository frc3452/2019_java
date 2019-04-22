package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.pathadapter.PathAdapter;
import frc.robot.poofs.util.math.Rotation2d;

public class Rocket_Far_Turn_Around_Same extends PathContainer {
    public Rocket_Far_Turn_Around_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(271, 275, 0, 30));
        sWaypoints.add(new Waypoint(227, 263, 15, 60));
        sWaypoints.add(new Waypoint(120, 298.28, 15, 60));
        sWaypoints.add(new Waypoint(39, 298.28, 0, 60).setFieldAdaption(PathAdapter.feederStation));
    }

    @Override
    public boolean isReversed() {
        return true;
    }

    @Override
    public Rotation2d getEndGyroMovement() {
        return Rotation2d.fromDegrees(180);
    }
}