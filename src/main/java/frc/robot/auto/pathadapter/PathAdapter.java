package frc.robot.auto.pathadapter;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.auto.pathadapter.fieldprofiles.FieldProfile;
import frc.robot.auto.pathadapter.fieldprofiles.ReferenceField;
import frc.robot.auto.pathadapter.robotprofiles.CompBot;
import frc.robot.auto.pathadapter.robotprofiles.RobotProfile;
import frc.robot.poofs.util.math.Translation2d;

public class PathAdapter {
    private static final DriverStation ds = DriverStation.getInstance();

    private static FieldProfile kReferenceField = new ReferenceField();
    private static FieldProfile kCurrentField = new ReferenceField();

    private static RobotProfile kRobotProfile = new CompBot();

    public static double getWheelDiameterInches() {
        return kRobotProfile.getWheelDiamaterInches();
    }

    public static double getTrackWidthInches() {
        return kRobotProfile.getTrackWidthInces();
    }

    public static double getWheelBaseInches() {
        return kRobotProfile.getWheelBaseInches();
    }
}