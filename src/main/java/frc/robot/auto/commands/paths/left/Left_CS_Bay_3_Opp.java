package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;
import frc.robot.ConfigurableDrive.Rotation2d;

public class Left_CS_Bay_3_Opp extends PathContainer {
    public Left_CS_Bay_3_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(124, 205, 15, 30));
        sWaypoints.add(new Waypoint(216, 97, 15, 60));
        sWaypoints.add(new Waypoint(304.25, 98, 0, 60).setFieldAdaption(PathAdapter.cargoShipBay3));
    }

    @Override
    public boolean isReversed() {
        return false;
    }

    @Override
    public Rotation2d getEndGyroMovement() {
        return Rotation2d.fromDegrees(270);
    }
}