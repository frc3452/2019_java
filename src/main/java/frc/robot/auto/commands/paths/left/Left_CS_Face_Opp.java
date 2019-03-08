package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.pathadapter.PathAdapter;

public class Left_CS_Face_Opp extends PathContainer {
    public Left_CS_Face_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(130, 205, 15, 30));
        sWaypoints.add(new Waypoint(154, 151, 15, 60).setFieldAdaption(PathAdapter.cargoShipFace));
        sWaypoints.add(new Waypoint(180, 151, 1, 30).setFieldAdaption(PathAdapter.cargoShipFace));
        sWaypoints.add(new Waypoint(200.75, 151, 0, 30).setFieldAdaption(PathAdapter.cargoShipFace));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}