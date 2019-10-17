package frc.robot.auto.pathadapter.fieldprofiles;

import frc.robot.Constants;

public class PracticeField extends FieldProfile {
    private HeightsContainer mCompHeightsAtWorlds;
    private HeightsContainer mPracticeHeights;

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
        initHeights();
    }

    private void initHeights() {
        initCompHeights();
        initPracticeHeights();
    }

    private void initPracticeHeights() {
        var p = new HeightsContainer.HeightsBuilder();
        p.zero = 16.5;
        p.home = p.zero = 0.5;
        p.hp_floor_grab = p.zero + 1;
        p.hp1 = 19.5 + 1;
        p.hp2 = 47 + 3 + 1;
        p.hp3 = 75 + 3 + 1;
        p.cargo_ship = 46;
        p.cargo1 = 27.5 + 1.5 + 1.0;
        p.cargo2 = 55.5 + 1 + 2;
        p.cargo3 = 83.5 + 1 + 3;
        p.hp_feed_jog = p.hp1 + 5;
        p.lowest_with_slides_out = 22.0;
        p.ticks_per_inch = 350;
        p.hatch_place_jog = 3;
        mPracticeHeights = p.build();
    }

    private void initCompHeights() {
        var comp = new HeightsContainer.HeightsBuilder();
        comp.zero = 16.5;
        comp.hp_floor_grab = comp.zero + 1;
        comp.hp1 = 20 + 3 - 1.5;
        comp.hp2 = 47 + 3;
        comp.hp3 = 75 + 3 - 2.0;
        comp.cargo_ship = 46;
        comp.cargo1 = 28.5 + 2.5;
        comp.cargo2 = 56.5 + 2.5 + 2.5 - 3;
        comp.cargo3 = 83.5 + 2.5;
        comp.hp_feed_jog = comp.hp1 + 5;
        comp.lowest_with_slides_out = 22.0;
        comp.home = comp.zero + 0.5;
        comp.ticks_per_inch = 350;
        comp.hatch_place_jog = 3;
        mCompHeightsAtWorlds = comp.build();
    }

    @Override
    public HeightsContainer getElevatorHeights() {
        if (Constants.COMP_BOT) {
            return mCompHeightsAtWorlds;
        } else {
            return mPracticeHeights;
        }
    }
}