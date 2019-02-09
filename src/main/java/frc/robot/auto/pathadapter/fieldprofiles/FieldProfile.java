package frc.robot.auto.pathadapter.fieldprofiles;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Translation2d;

public class FieldProfile {

    private static final double mCenterLineY = 162;
    private static final double mFieldWith = mCenterLineY * 2.0;
    private static final double mMidFieldLineX = 324;

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

    /**
     * public FieldValues<Double> mCargoShipTeam1BayToCenterLine; public
     * FieldValues<Double> mCargoShipTeam1BayToSideWall; public FieldValues<Double>
     * mCargoShipTeam2BayToCenterLine; public FieldValues<Double>
     * mCargoShipTeam2BayToSideWall; public FieldValues<Double>
     * mCargoShipTeam3BayToCenterLine; public FieldValues<Double>
     * mCargoShipTeam3BayToSideWall; public FieldValues<Double>
     * mRocketEdgeToDriverStation; public FieldValues<Double> mRocketFaceToWall;
     * public FieldValues<Double> mCargoShipCenterOfTapeToCenterLine; public
     * FieldValues<Double> mCargoShipFaceToEdgeOfPlatform;
     */

    public Translation2d getLeftFeederStation(Alliance a) {
        final double x = mFeederStationToDriverWall.getLeft(a);
        final double y = mFieldWith - mFeederStationToDriverWall.getLeft(a);
        Translation2d ret = new Translation2d(x, y);
        return ret;
    }

    public Translation2d getRightFeederStation(Alliance a) {
        final double x = mFeederStationToDriverWall.getRight(a);
        final double y = mFeederStationToDriverWall.getRight(a);
        Translation2d ret = new Translation2d(x, y);
        return ret;
    }

    public Translation2d getCargoShipFrontFace(Alliance a, boolean left) {
        final double x = mHABLevel3EdgeToDriverStation.get(a, left) + mHABLevel3EdgeToDriverStation.get(a, left)
        + mHABRamp.get(a, left) + mCargoShipFaceToHABRamp.get(a, left);

        //If on right, y value less than centerLine
        final double y = mCenterLineY + (left ? 1 : -1) * mCargoShipFaceToMidLine.get(a,left);

        Translation2d trans2d = new Translation2d(x, y);
        return trans2d;
    }
}