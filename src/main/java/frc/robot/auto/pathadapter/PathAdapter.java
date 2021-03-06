package frc.robot.auto.pathadapter;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.auto.pathadapter.fieldprofiles.*;
import frc.robot.auto.pathadapter.robotprofiles.PracticeBot;
import frc.robot.auto.pathadapter.robotprofiles.RobotProfile;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;

public class PathAdapter {
    public static FieldProfile kReferenceField = new ReferenceField();
    public static FieldProfile kCurrentField = new PracticeField();

    private static RobotProfile kRobotProfile = new PracticeBot();

    public static final double inchesFromRocket = 13;
    public static final double inchesFromCargoShipSide = 10;
    public static final double inchesFromCargoShipFace = 5;
    public static final double mInchesFromFeederStation = 10;

    public static final FieldValues<Translation2d> feederStation;
    public static final FieldValues<Translation2d> cargoShipBay1;
    public static final FieldValues<Translation2d> cargoShipBay2;
    public static final FieldValues<Translation2d> cargoShipBay3;
    public static final FieldValues<Translation2d> cargoShipFace;
    public static final FieldValues<Translation2d> rocketNear;
    public static final FieldValues<Translation2d> rocketMid;
    public static final FieldValues<Translation2d> rocketFar;

    static {

        {
            Translation2d translation = Translation2d.identity();
            translation.translateBy(inchesFromRocket, Rotation2d.fromDegrees(180 + 61.25));

            rocketNear = new FieldValues<Translation2d>(translation, translation.getFlippedY());
        }

        {
            rocketMid = new FieldValues<>(new Translation2d(0, -inchesFromRocket),
                    new Translation2d(0, inchesFromRocket));
        }

        {
            Translation2d translation = new Translation2d();
            translation.translateBy(inchesFromRocket, Rotation2d.fromDegrees(360 - 61.25));

            rocketFar = new FieldValues<Translation2d>(translation, translation.getFlippedY());
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

            cargoShipBay1 = new FieldValues<>(new Translation2d(0, inchesFromCargoShipSide),
                    new Translation2d(0, -inchesFromCargoShipSide));
            cargoShipBay2 = cargoShipBay1;
            cargoShipBay3 = cargoShipBay1;
            cargoShipFace = new FieldValues<>(new Translation2d(-inchesFromCargoShipFace, 0));
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