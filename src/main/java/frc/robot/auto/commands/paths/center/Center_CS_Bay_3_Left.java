package frc.robot.auto.commands.paths.center;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.pathadapter.PathAdapter;
import frc.robot.ConfigurableDrive.Rotation2d;

public class Center_CS_Bay_3_Left extends PathContainer {
    public Center_CS_Bay_3_Left() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 162, 0, 0));
        sWaypoints.add(new Waypoint(130, 162, 30, 30));
        sWaypoints.add(new Waypoint(257, 227, 30, 60));
        sWaypoints.add(new Waypoint(304.25, 227, 0, 30).setFieldAdaption(PathAdapter.cargoShipBay3));
    }

    @Override
    public boolean isReversed() {
        return false;
    }

    @Override
    public Rotation2d getEndGyroMovement() {
        return Rotation2d.fromDegrees(90);
    }
}