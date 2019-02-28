package frc.robot.auto.pathadapter;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.fieldprofiles.FieldProfile;
import frc.robot.auto.pathadapter.fieldprofiles.HeightsContainer;
import frc.robot.auto.pathadapter.fieldprofiles.PracticeField;
import frc.robot.auto.pathadapter.fieldprofiles.ReferenceField;
import frc.robot.auto.pathadapter.robotprofiles.PracticeBot;
import frc.robot.auto.pathadapter.robotprofiles.RobotProfile;
import frc.robot.poofs.util.math.Translation2d;

public class PathAdapter {
    private static final DriverStation ds = DriverStation.getInstance();

    public static FieldProfile kReferenceField = new ReferenceField();
    public static FieldProfile kCurrentField = new PracticeField();

    private static RobotProfile kRobotProfile = new PracticeBot();

    public static Alliance g() {
        return ds.getAlliance();
    }

    public static Waypoint getFeederStation(Waypoint input, boolean left) {
        Translation2d translation = kCurrentField.getFeederStation(g(), left)
                .translateBy(kReferenceField.getFeederStation(g(), left).inverse()); //inverse

        System.out.println("Translation: " + translation);

        input.translateBy(translation);
        return input;
    }

    public static Waypoint getCargoShipBay(Waypoint input, int bay, boolean left) {
        Translation2d translation = kCurrentField.getBay(g(), bay, left)
                .translateBy(kReferenceField.getBay(g(), bay, left).inverse());

        input.translateBy(translation);
        return input;
    }

    public static Waypoint getCargoShipFace(Waypoint input, boolean left) {
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