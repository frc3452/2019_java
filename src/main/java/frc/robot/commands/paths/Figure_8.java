
package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Figure_8 extends PathContainer {

    public Figure_8() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(101, 213, 1, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));
        sWaypoints.add(new Waypoint(182, 252, 15, 50));
        sWaypoints.add(new Waypoint(220, 215, 1, 50));
        sWaypoints.add(new Waypoint(257, 176, 15, 50));
        sWaypoints.add(new Waypoint(295, 215, 15, 50));
        sWaypoints.add(new Waypoint(257, 251, 15, 50));
        sWaypoints.add(new Waypoint(182, 176, 15, 50));
        sWaypoints.add(new Waypoint(145, 217, 0, 50));

        System.out.println("FIGURE 8: " + sWaypoints.size());
    }

    @Override
    public boolean isReversed() {
        return false;
    }

}