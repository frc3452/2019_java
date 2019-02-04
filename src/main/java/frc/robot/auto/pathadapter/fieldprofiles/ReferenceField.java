package frc.robot.auto.pathadapter.fieldprofiles;

import frc.robot.auto.pathadapter.fieldprofiles.FieldValues;

public class ReferenceField extends FieldProfile {
    public ReferenceField() {
        // this.mHABLevel3ToWall = new FieldValues<Double>(137.245);
       
        this.mHABLevel3ToFeederStation = new FieldValues<Double>(110.28);

        this.mCargoShipTeam1BayToCenterLine = new FieldValues<Double>(10.0 + (7 / 8));

        this.mCargoShipTeam2BayToTeam1Bay = new FieldValues<Double>(19.75);

        this.mCargoShipTeam3BayToTeam2Bay = new FieldValues<Double>(19.75);

        this.mCargoShipTeam1BayToSideWall = new FieldValues<Double>(115.125);

        this.mCargoShipTeam2BayToSideWall = new FieldValues<Double>(115.125);

        this.mCargoShipTeam3BayToSideWall = new FieldValues<Double>(115.125);

        this.mRocketCenterTapeToDriverStation = new FieldValues<Double>(228.3125);

        //TODO TUNE
        this.mRocketCenterTapeToWall = new FieldValues<Double>(-1.0);

        this.mCargoShipTapeLineToWall = new FieldValues<Double>(149.0 + (3 / 8));

        this.mCargoShipTapeLineToFaceOfHAB = new FieldValues<Double>(154.0);
    }
}