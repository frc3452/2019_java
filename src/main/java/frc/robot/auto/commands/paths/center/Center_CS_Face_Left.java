package frc.robot.auto.commands.paths.center;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.pathadapter.PathAdapter;

public class Center_CS_Face_Left extends PathContainer {
    public Center_CS_Face_Left() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 162, 0, 0));
        sWaypoints.add(new Waypoint(130 - 10, 162, 15, 30));
        sWaypoints.add(new Waypoint(155.991, 172.88, 15, 60).setFieldAdaption(PathAdapter.cargoShipFace));
        sWaypoints.add(new Waypoint(178.3705, 172.88, 1, 60).setFieldAdaption(PathAdapter.cargoShipFace));
        sWaypoints.add(new Waypoint(200.75, 172.88, 0, 30).setFieldAdaption(PathAdapter.cargoShipFace));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}