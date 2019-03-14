package frc.robot.auto.pathadapter.fieldprofiles;

import frc.robot.Constants.kElevator;

public class MISJO extends FieldProfile {
    public MISJO() {
        this.mFeederStationToDriverWall = new FieldValues<Double>(0.0);
        this.mFeederStationToWall = new FieldValues<Double>(25.72);
        this.mBay1ToWall = new FieldValues<Double>(133.13);
        this.mBay2ToWall = new FieldValues<Double>(133.13);
        this.mBay3ToWall = new FieldValues<Double>(133.13);
        this.mBay1ToRamp = new FieldValues<Double>(163.52);
        this.mBay2ToRamp = new FieldValues<Double>(207.02);
        this.mBay3ToRamp = new FieldValues<Double>(250.52);
        this.mCargoShipFaceToHABRamp = new FieldValues<Double>(123.97);
        this.mCargoShipFaceToMidLine = new FieldValues<Double>(10.875);
        this.mHABLevel1EdgeToRamp = new FieldValues<Double>(40.7387);
        this.mHABLevel3EdgeToDriverStation = new FieldValues<Double>(40.0);
        this.mHABRamp = new FieldValues<Double>(10.9632);
    }

    private HeightsContainer mHeights = new HeightsContainer() {

        private final double mod = 0.5;

        @Override
        public double hp_floor_Grab() {
            return kElevator.HOME_INCHES + 1;
        }

        @Override
        public double hp1() {
            return 19 + mod + 3;
        }

        @Override
        public double hp2() {
            return 47 + mod;
        }

        @Override
        public double hp3() {
            return 75 + mod;
        }

        @Override
        public double cargo_ship() {
            return 46 + mod;
        }

        @Override
        public double cargo1() {
            return 27.5 + mod + 1.5;
        }

        @Override
        public double cargo2() {
            return 55.5 + mod;
        }

        @Override
        public double cargo3() {
            return 83.5 + mod - 1;
        }

        @Override
        public double hp_feed_jog() {
            return hp1() + 5 + mod;
        }
    };

    @Override
    public HeightsContainer getElevatorHeights() {
        return mHeights;
    }

}