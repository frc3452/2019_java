package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.pathadapter.PathAdapter;

public class Left_CS_Face_Same extends PathContainer {
    public Left_CS_Face_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(130, 205, 15, 60));
        sWaypoints.add(new Waypoint(155.991, 172.88, 15, 60).setFieldAdaption(PathAdapter.cargoShipFace));
        sWaypoints.add(new Waypoint(178.3705, 172.88, 1, 30).setFieldAdaption(PathAdapter.cargoShipFace));
        sWaypoints.add(new Waypoint(200.75, 172.88, 0, 30).setFieldAdaption(PathAdapter.cargoShipFace));
    }
    

    @Override
    public boolean isReversed() {
        return false;
    }
}