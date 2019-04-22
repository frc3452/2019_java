package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.pathadapter.PathAdapter;
import frc.robot.poofs.util.math.Rotation2d;

public class CS_Face_Turn_Around_Same extends PathContainer {
    public CS_Face_Turn_Around_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(190.75, 172.88, 0, 30));
        sWaypoints.add(new Waypoint(169, 172.88, 15, 30));
        sWaypoints.add(new Waypoint(118, 245, 15, 60));
        sWaypoints.add(new Waypoint(76, 298.28, 15, 60));
        sWaypoints.add(new Waypoint(33.2545, 298.28, 0, 30).setFieldAdaption(PathAdapter.feederStation));
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