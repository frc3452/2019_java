package frc.robot.auto.pathadapter.fieldprofiles;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.poofs.util.math.Translation2d;

public abstract class FieldProfile {

    public static final double centerLineY = 162;
    public static final double mFieldWith = centerLineY * 2.0;
    public static final double midFieldLineX = 324;

    // Feeder
    public FieldValues<Double> mFeederStationToDriverWall;
    public FieldValues<Double> mFeederStationToWall;

    // HAB
    public FieldValues<Double> mHABLevel3EdgeToDriverStation;
    public FieldValues<Double> mHABLevel1EdgeToRamp;
    public FieldValues<Double> mHABRamp;

    // Cargo ship
    public FieldValues<Double> mCargoShipFaceToHABRamp;
    public FieldValues<Double> mCargoShipFaceToMidLine;

    // Left Cargo Ship Bay 1
    public FieldValues<Double> mBay1ToWall;
    public FieldValues<Double> mBay1ToRamp;

    // Left Cargo Ship Bay 2
    public FieldValues<Double> mBay2ToWall;
    public FieldValues<Double> mBay2ToRamp;

    // Left Cargo Ship Bay 3
    public FieldValues<Double> mBay3ToWall;
    public FieldValues<Double> mBay3ToRamp;

    public Translation2d getFeederStation(Alliance a, boolean left) {
        if (left)
            return getLeftFeederStation(a);

        return getRightFeederStation(a);
    }

    public Translation2d getLeftFeederStation(Alliance a) {
        final double x = mFeederStationToDriverWall.getLeft(a);
        final double y = mFieldWith - mFeederStationToWall.getLeft(a);
        Translation2d ret = new Translation2d(x, y);
        return ret;
    }

    public Translation2d getRightFeederStation(Alliance a) {
        final double x = mFeederStationToDriverWall.getRight(a);
        final double y = mFeederStationToWall.getRight(a);
        Translation2d ret = new Translation2d(x, y);
        return ret;
    }

    // Cargo ship side bays
    // -----------------------------------------------------------------
    public Translation2d getBay1Left(Alliance a) {
        final double x = mBay1ToWall.getLeft(a);
        final double y = mHABLevel3EdgeToDriverStation.getLeft(a) + mHABLevel1EdgeToRamp.getLeft(a)
                + mHABRamp.getLeft(a) + mBay1ToRamp.getLeft(a);
        Translation2d ret = new Translation2d(x, y);
        return ret;
    }

    private FieldValues<Double> getBayToWall(int bay) {
        switch (bay) {
            case 1:
                return mBay1ToWall;
            case 2:
                return mBay2ToWall;
            case 3:
                return mBay3ToWall;
            default:
                return null;
        }
    }

    private FieldValues<Double> getBayToRamp(int bay) {
        switch (bay) {
            case 1:
                return mBay1ToRamp;
            case 2:
                return mBay2ToRamp;
            case 3:
                return mBay3ToRamp;
            default:
                return null;
        }
    }

    public Translation2d getBay(Alliance a, int bay, boolean left) {
        final double x = mHABLevel3EdgeToDriverStation.get(a, left) + mHABLevel1EdgeToRamp.get(a, left)
                + mHABRamp.get(a, left) + getBayToRamp(bay).get(a, left);
        final double y;

        if (left) {
            y = mFieldWith - getBayToWall(bay).get(a, left);
        } else {
            y = getBayToWall(bay).get(a, left);
        }

        Translation2d ret = new Translation2d(x, y);
        return ret;
    }

    public Translation2d getBay1Right(Alliance a) {
        final double x = mBay1ToWall.getRight(a);
        final double y = mHABLevel3EdgeToDriverStation.getRight(a) + mHABLevel1EdgeToRamp.getRight(a)
                + mHABRamp.getRight(a) + mBay1ToRamp.getRight(a);
        Translation2d ret = new Translation2d(x, y);
        return ret;
    }

    public Translation2d getBay2Left(Alliance a) {
        final double x = mBay2ToWall.getLeft(a);
        final double y = mBay2ToRamp.getLeft(a);
        Translation2d ret = new Translation2d(x, y);
        return ret;
    }

    public Translation2d getBay2Right(Alliance a) {
        final double x = mBay2ToWall.getRight(a);
        final double y = mBay2ToRamp.getRight(a);
        Translation2d ret = new Translation2d(x, y);
        return ret;
    }

    public Translation2d getBay3Left(Alliance a) {
        final double x = mBay3ToWall.getLeft(a);
        final double y = mBay3ToRamp.getLeft(a);
        Translation2d ret = new Translation2d(x, y);
        return ret;
    }

    public Translation2d getBay3Right(Alliance a) {
        final double x = mBay3ToWall.getRight(a);
        final double y = mBay3ToRamp.getRight(a);
        Translation2d ret = new Translation2d(x, y);
        return ret;
    }

    public Translation2d getCargoShipFrontFace(Alliance a, boolean left) {
        final double x = mHABLevel3EdgeToDriverStation.get(a, left) + mHABLevel1EdgeToRamp.get(a, left)
                + mHABRamp.get(a, left) + mCargoShipFaceToHABRamp.get(a, left);

        // If on right, y value less than centerLine
        final double y = centerLineY + (left ? 1 : -1) * mCargoShipFaceToMidLine.get(a, left);

        Translation2d trans2d = new Translation2d(x, y);
        return trans2d;
    }

    public abstract HeightsContainer getElevatorHeights();
}