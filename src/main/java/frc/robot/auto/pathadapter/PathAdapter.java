package frc.robot.auto.pathadapter;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.auto.pathadapter.fieldprofiles.FieldProfile;
import frc.robot.auto.pathadapter.fieldprofiles.FieldValues;
import frc.robot.auto.pathadapter.fieldprofiles.HeightsContainer;
import frc.robot.auto.pathadapter.fieldprofiles.PracticeField;
import frc.robot.auto.pathadapter.fieldprofiles.ReferenceField;
import frc.robot.auto.pathadapter.robotprofiles.PracticeBot;
import frc.robot.auto.pathadapter.robotprofiles.RobotProfile;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;

public class PathAdapter {
    private static final DriverStation ds = DriverStation.getInstance();

    public static FieldProfile kReferenceField = new ReferenceField();
    public static FieldProfile kCurrentField = new PracticeField();

    private static RobotProfile kRobotProfile = new PracticeBot();

    private static final double mInchesFromRocket = 10;
    private static final double mInchesFromCargoShipSide = 10;
    private static final double mInchesFromCargoShipFace = 10;
    private static final double mInchesFromFeederStation = 10;

    public static final FieldValues<Translation2d> feederStation;
    public static final FieldValues<Translation2d> cargoShipBay1;
    public static final FieldValues<Translation2d> cargoShipBay2;
    public static final FieldValues<Translation2d> cargoShipBay3;
    public static final FieldValues<Translation2d> cargoShipFace;
    public static final FieldValues<Translation2d> rocketNear;
    public static final FieldValues<Translation2d> rocketMid = null;
    public static final FieldValues<Translation2d> rocketFar = null;

    static {

        {
            Translation2d translation = Translation2d.identity();
            translation.translateBy(mInchesFromRocket, Rotation2d.fromDegrees(270 - 61.25));

            rocketNear = new FieldValues<Translation2d>(translation, translation.getFlippedY());

        }

        {
            // ArrayList<Translation2d> arr = Translation2d.getArray();

            // // RedLeft RedRight BlueLeft BlueRight
            // for (int color = 0; color < 2; color++) {
            // for (int left = 0; left < 2; left++) {
            // arr.add(kCurrentField.getFeederStation(a(color), l(left))
            // .translateBy(kReferenceField.getFeederStation(a(color), l(left)).inverse()));
            // }
            // }
            // feederStation = new FieldValues<>(arr);
        }

        {
            // ArrayList<Translation2d> arr = Translation2d.getArray();

            // for (int color = 0; color < 2; color++) {
            // for (int left = 0; left < 2; left++) {
            // arr.add(kCurrentField.getBay(a(color), 1, l(left))
            // .translateBy(kReferenceField.getBay(a(color), 1, l(left)).inverse()));
            // }
            // }

            cargoShipBay1 = new FieldValues<>(new Translation2d(0, mInchesFromCargoShipSide),
                    new Translation2d(0, -mInchesFromCargoShipSide));
            cargoShipBay2 = cargoShipBay1;
            cargoShipBay3 = cargoShipBay1;
            cargoShipFace = new FieldValues<>(new Translation2d(-mInchesFromCargoShipFace, 0));
            feederStation = new FieldValues<>(new Translation2d(mInchesFromFeederStation, 0));

            // cargoShipBay1 = new FieldValues<>(arr);
        }

        {
            // ArrayList<Translation2d> arr = Translation2d.getArray();

            // for (int color = 0; color < 2; color++) {
            // for (int left = 0; left < 2; left++) {
            // arr.add(kCurrentField.getBay(a(color), 2, l(left))
            // .translateBy(kReferenceField.getBay(a(color), 2, l(left)).inverse()));
            // }
            // }
            // cargoShipBay2 = new FieldValues<>(arr);
        }

        {
            // ArrayList<Translation2d> arr = Translation2d.getArray();

            // for (int color = 0; color < 2; color++) {
            // for (int left = 0; left < 2; left++) {
            // arr.add(kCurrentField.getBay(a(color), 3, l(left))
            // .translateBy(kReferenceField.getBay(a(color), 3, l(left)).inverse()));
            // }
            // }
            // cargoShipBay3 = new FieldValues<>(arr);
        }
        {
            // ArrayList<Translation2d> arr = Translation2d.getArray();
            // for (int color = 0; color < 2; color++) {
            // for (int left = 0; left < 2; left++) {
            // arr.add(kCurrentField.getCargoShipFrontFace(a(color), l(left))
            // .translateBy(kReferenceField.getCargoShipFrontFace(a(color),
            // l(left)).inverse()));
            // }
            // }
            // cargoShipFace = new FieldValues<>(arr);
        }
    }

    private static Alliance a(int input) {
        if (input == 0)
            return Alliance.Red;

        return Alliance.Blue;
    }

    private static boolean l(int input) {
        return input == 0;
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