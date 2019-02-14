package frc.robot.auto.pathadapter;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.auto.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.fieldprofiles.FieldProfile;
import frc.robot.auto.pathadapter.fieldprofiles.HeightsContainer;
import frc.robot.auto.pathadapter.fieldprofiles.ReferenceField;
import frc.robot.auto.pathadapter.robotprofiles.PracticeBot;
import frc.robot.auto.pathadapter.robotprofiles.RobotProfile;
import frc.robot.poofs.util.math.Translation2d;

public class PathAdapter {
    private static final DriverStation ds = DriverStation.getInstance();

    private static FieldProfile kReferenceField = new ReferenceField();
    private static FieldProfile kCurrentField = new ReferenceField();

    private static RobotProfile kRobotProfile = new PracticeBot();

    public static Alliance g() {
        return ds.getAlliance();
    }

    public static Waypoint getFeederStation(Waypoint input, boolean left) {
        Translation2d translation = kCurrentField.getFeederStation(g(), left)
                .translateBy(kReferenceField.getFeederStation(g(), left).inverse());

        input.translateBy(translation);
        return input;
    }

    public static Waypoint getHABBay(Waypoint input, int bay, boolean left) {
        Translation2d translation = kCurrentField.getBay(g(), bay, left)
                .translateBy(kReferenceField.getBay(g(), bay, left).inverse());

        input.translateBy(translation);
        return input;
    }

    public static Waypoint getHABFace(Waypoint input, boolean left) {
        Translation2d translation = kCurrentField.getCargoShipFrontFace(g(), left)
                .translateBy(kReferenceField.getCargoShipFrontFace(g(), left).inverse());

        input.translateBy(translation);
        return input;
    }

    public static double getWheelDiameterInches() {
        return kRobotProfile.getWheelDiamaterInches();
    }

    public static double getTrackWidthInches() {
        return kRobotProfile.getTrackWidthInces();
    }

    public static double getWheelBaseInches() {
        return kRobotProfile.getWheelBaseInches();
    }

    public static HeightsContainer getHeights() {
        return kCurrentField.getElevatorHeights();
    }
}