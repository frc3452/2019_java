package frc.robot.auto.pathadapter.fieldprofiles;

import frc.robot.Constants.kElevator;

public class ReferenceField extends FieldProfile {
    public ReferenceField() {
    }

    private HeightsContainer mHeight = new HeightsContainer() {
        @Override
        public double home() {
            return kElevator.HOME_INCHES;
        }

        @Override
        public double hp_floor_Grab() {
            return 17;
        }

        @Override
        public double hp1() {
            return 19;
        }

        @Override
        public double hp2() {
            return 47;
        }

        @Override
        public double hp3() {
            return 75;
        }

        @Override
        public double cargo_ship() {
            return 46;
        }

        @Override
        public double cargo1() {
            return 27.5;
        }

        @Override
        public double cargo2() {
            return 55.5;
        }

        @Override
        public double cargo3() {
            return 83.5;
        }

        @Override
        public double hp_feed_jog() {
            return hp1() + 2;
        }
    };

    @Override
    public HeightsContainer getElevatorHeights() {
        return mHeight;
    }

}