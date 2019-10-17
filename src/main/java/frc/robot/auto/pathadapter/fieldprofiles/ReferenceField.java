package frc.robot.auto.pathadapter.fieldprofiles;

public class ReferenceField extends FieldProfile {
    private HeightsContainer mHeight;

    public ReferenceField() {
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
        var b = new HeightsContainer.HeightsBuilder();
        b.zero = 16;
        b.home = 16.25;
        b.hp_floor_grab = b.zero + 1;
        b.hp1 = 19;
        b.hp2 = 47;
        b.hp3 = 75;
        b.cargo_ship = 46;
        b.cargo1 = 27.5;
        b.cargo2 = 55.5;
        b.cargo3 = 83.5;
        b.hp_feed_jog = b.hp1 + 5;
        b.lowest_with_slides_out = 17.5;
        b.ticks_per_inch = 353;
        b.hatch_place_jog = 4;
        mHeight = b.build();
    }

    @Override
    public HeightsContainer getElevatorHeights() {
        return mHeight;
    }

}