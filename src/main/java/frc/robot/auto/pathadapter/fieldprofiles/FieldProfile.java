package frc.robot.auto.pathadapter.fieldprofiles;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.poofs.util.math.Translation2d;

public class FieldProfile {

    private static final double mCenterLineY = 162;
    private static final double mFieldWith = mCenterLineY * 2.0;
    private static final double mMidFieldLineX = 324;

    public FieldValues<Double> mHABLevel3ToFeederStation;

    public FieldValues<Double> mCargoShipTeam1BayToCenterLine;
    public FieldValues<Double> mCargoShipTeam1BayToSideWall;

    public FieldValues<Double> mCargoShipTeam2BayToCenterLine;
    public FieldValues<Double> mCargoShipTeam2BayToSideWall;

    public FieldValues<Double> mCargoShipTeam3BayToCenterLine;
    public FieldValues<Double> mCargoShipTeam3BayToSideWall;

    public FieldValues<Double> mRocketEdgeToDriverStation;
    public FieldValues<Double> mRocketFaceToWall;

    public FieldValues<Double> mCargoShipCenterOfTapeToCenterLine;
    public FieldValues<Double> mCargoShipFaceToEdgeOfPlatform;

    // TODO CHECK IF MEASUREMENTS ARE TO EDGE OF TAPE OR MIDDLE, ADJUST ACCORDINGLY
    public Translation2d getFeederStation(Alliance a, boolean left) {
        Translation2d ret = Translation2d.identity();
        final double x = 0;
        final double y = ((left ? 1 : -1) * mHABLevel3ToFeederStation.get(a, left) + mCenterLineY + (left ? 1 : -1));
        ret.setX(x);
        ret.setY(y);
        return ret;
    }

    public Translation2d getCargoBay1(Alliance a, boolean left) {
        Translation2d ret = Translation2d.identity();
        final double x = mMidFieldLineX - mCargoShipTeam1BayToCenterLine.get(a, left) - 1;
        final double y;
        if (left) {
            y = mFieldWith - mCargoShipTeam1BayToSideWall.get(a, left);
        } else {
            y = mCargoShipTeam1BayToSideWall.get(a, left);
        }
        ret.setX(x);
        ret.setY(y);
        return ret;
    }

    public Translation2d getCargoBay2(Alliance a, boolean left) {
        Translation2d ret = Translation2d.identity();
        final double x = mMidFieldLineX - mCargoShipTeam1BayToCenterLine.get(a, left)
                - mCargoShipTeam2BayToTeam1Bay.get(a, left) - 1;
        final double y;
        if (left) {
            y = mFieldWith - mCargoShipTeam2BayToSideWall.get(a, left);
        } else {
            y = mCargoShipTeam2BayToSideWall.get(a, left);
        }
        ret.setX(x);
        ret.setY(y);
        return ret;
    }

    public Translation2d getCargoBay3(Alliance a, boolean left) {
        Translation2d ret = Translation2d.identity();
        final double x = mMidFieldLineX - mCargoShipTeam1BayToCenterLine.get(a, left)
                - mCargoShipTeam2BayToTeam1Bay.get(a, left) - mCargoShipTeam3BayToTeam2Bay.get(a, left) - 1;
        final double y;
        if (left) {
            y = mFieldWith - mCargoShipTeam3BayToSideWall.get(a, left);
        } else {
            y = mCargoShipTeam3BayToSideWall.get(a, left);
        }
        ret.setX(x);
        ret.setY(y);
        return ret;
    }

    //NEED IMAGE FOR
    public Translation2d getRocket(Alliance a, boolean left) {
        Translation2d ret = Translation2d.identity();
        final double x = mRocketEdgeToDriverStation.get(a, left) + 1;
        final double y;
        if (left)
        {
            y = mFieldWith - mRocketFaceToWall.get(a, left);
        } else {
            y = 0;
        }
        ret.setX(x);
        ret.setY(y);
        return ret;
    }

}