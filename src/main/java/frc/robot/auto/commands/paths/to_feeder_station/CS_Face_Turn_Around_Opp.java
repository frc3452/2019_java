package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.pathadapter.PathAdapter;
import frc.robot.ConfigurableDrive.Rotation2d;

public class CS_Face_Turn_Around_Opp extends PathContainer {
    public CS_Face_Turn_Around_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(190.75, 172.88, 0, 30));
        sWaypoints.add(new Waypoint(154, 172, 15, 30));
        sWaypoints.add(new Waypoint(124, 77, 15, 60));
        sWaypoints.add(new Waypoint(109, 50, 15, 60));
        sWaypoints.add(new Waypoint(68.88, 25.72, 15, 60));
        sWaypoints.add(new Waypoint(44.19, 25.719999999999995, 0, 30).setFieldAdaption(PathAdapter.feederStation));
    }

    @Override
    public boolean isReversed() {
        return false;
    }

    @Override
    public Rotation2d getStartGyroMovement() {
        return getStartRotation();
    }
}