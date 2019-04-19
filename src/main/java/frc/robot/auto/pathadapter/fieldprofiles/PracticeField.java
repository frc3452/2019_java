package frc.robot.auto.pathadapter.fieldprofiles;

import frc.robot.Constants;

public class PracticeField extends FieldProfile {
    public PracticeField() {
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

    private HeightsContainer mPracticeHeights = new HeightsContainer() {
        @Override
        public double hp_floor_Grab() {
            return zero() + 1;
        }

        @Override
        public double hp1() {
            // return 19; //19

            // return 22;
            // return 21.5;
            return 19.5;
        }

        @Override
        public double hp2() {
            return 47 + 3;
        }

        @Override
        public double hp3() {
            return 75 + 3;
        }

        @Override
        public double cargo_ship() {
            return 46;
        }

        @Override
        public double cargo1() {
            return 27.5 + 1.5 + 1;
        }

        @Override
        public double cargo2() {
            return 55.5 + 1 + 2;    
        }

        @Override
        public double cargo3() {
            return 83.5 + 1 + 3;
        }

        @Override
        public double hp_feed_jog() {
            // return hp1() + 7;
            return hp1() + 5; // big fat teeth
        }

        @Override
        public double zero() {
            return 16.5;
        }

        @Override
        public double lowest_with_slides_out() {
            // return 18.5;
            return 22.0; // big fat teeth
        }

        @Override
        public double home() {
            return zero() + 0.5;
        }

        @Override
        public int ticks_per_inch() {
            return 350;
        }

        @Override
        public double hatch_place_jog() {
            // return 4;
            return 3; // big fat teeth
        }
    };
    private HeightsContainer mCompHeights = new HeightsContainer() {
        @Override
        public double hp_floor_Grab() {
            return zero() + 1;
        }

        @Override
        public double hp1() {
            // return 19; //19

            // return 22;
            return 23; // big fat teeth
        }

        @Override
        public double hp2() {
            return 47 + 3;
        }

        @Override
        public double hp3() {
            return 75 + 3;
        }

        @Override
        public double cargo_ship() {
            return 46;
        }

        @Override
        public double cargo1() {
            return 27.5 + 2;
        }

        @Override
        public double cargo2() {
            return 55.5 + 2;
        }

        @Override
        public double cargo3() {
            return 83.5 + 2;
        }

        @Override
        public double hp_feed_jog() {
            // return hp1() + 7;
            return hp1() + 5; // big fat teeth
        }

        @Override
        public double zero() {
            return 16.5;
        }

        @Override
        public double lowest_with_slides_out() {
            // return 18.5;
            return 22.0; // big fat teeth
        }

        @Override
        public double home() {
            return zero() + 0.5;
        }

        @Override
        public int ticks_per_inch() {
            return 350;
        }

        @Override
        public double hatch_place_jog() {
            // return 4;
            return 3; // big fat teeth
        }
    };
    private HeightsContainer mCompHeightsAtMary = new HeightsContainer() {
        @Override
        public double hp_floor_Grab() {
            return zero() + 1;
        }

        @Override
        public double hp1() {
            // return 19; //19

            // return 22;
            return 23; // big fat teeth
        }

        @Override
        public double hp2() {
            return 47 + 3;
        }

        @Override
        public double hp3() {
            return 75 + 3;
        }

        @Override
        public double cargo_ship() {
            return 46;
        }

        @Override
        public double cargo1() {
            return 27.5 + 2;
        }

        @Override
        public double cargo2() {
            return 55.5 + 2;
        }

        @Override
        public double cargo3() {
            return 83.5 + 2;
        }

        @Override
        public double hp_feed_jog() {
            // return hp1() + 7;
            return hp1() + 5; // big fat teeth
        }

        @Override
        public double zero() {
            return 16.5;
        }

        @Override
        public double lowest_with_slides_out() {
            // return 18.5;
            return 22.0; // big fat teeth
        }

        @Override
        public double home() {
            return zero() + 0.5;
        }

        @Override
        public int ticks_per_inch() {
            return 350;
        }

        @Override
        public double hatch_place_jog() {
            // return 4;
            return 3; // big fat teeth
        }
    };
    private HeightsContainer mCompHeightsAtState = new HeightsContainer() {
        @Override
        public double hp_floor_Grab() {
            return zero() + 1;
        }

        @Override
        public double hp1() {
            // return 19; //19

            // return 22;
            return 20; // big fat teeth
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
            // was 2 too high
            return 28.5;
        }

        @Override
        public double cargo2() {
            // was 2 too high
            return 56.5;
        }

        @Override
        public double cargo3() {
            // was 2 too high
            return 84.5;
        }

        @Override
        public double hp_feed_jog() {
            // return hp1() + 7;
            return hp1() + 5; // big fat teeth
        }

        @Override
        public double zero() {
            return 16.5;
        }

        @Override
        public double lowest_with_slides_out() {
            // return 18.5;
            return 22.0; // big fat teeth
        }

        @Override
        public double home() {
            return zero() + 0.5;
        }

        @Override
        public int ticks_per_inch() {
            return 350;
        }

        @Override
        public double hatch_place_jog() {
            // return 4;
            return 3; // big fat teeth
        }
    };

    @Override
    public HeightsContainer getElevatorHeights() {
        if (Constants.COMP_BOT) {
            return mCompHeightsAtState;
        } else {
            return mPracticeHeights;
        }
    }

}