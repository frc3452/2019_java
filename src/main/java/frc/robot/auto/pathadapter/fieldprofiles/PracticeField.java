package frc.robot.auto.pathadapter.fieldprofiles;

import frc.robot.Constants.kElevator;

public class PracticeField extends FieldProfile {
    public PracticeField() {
        this.mFeederStationToDriverWall = new FieldValues<Double>(0.0);
        this.mFeederStationToWall = new FieldValues<Double>(5.5); // 5.5
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

    private HeightsContainer mHeights = new ReferenceField().getElevatorHeights();

    @Override
    public HeightsContainer getElevatorHeights() {
        return mHeights;
    }

}