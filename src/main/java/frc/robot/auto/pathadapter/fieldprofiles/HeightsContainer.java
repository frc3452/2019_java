package frc.robot.auto.pathadapter.fieldprofiles;

import frc.robot.Constants.kElevator;

public abstract class HeightsContainer {
    
    public double home() {
        return kElevator.HOME_INCHES + 1;
    }

    public abstract double hp_floor_Grab();

    public abstract double hp1();

    public abstract double hp2();

    public abstract double hp3();

    public abstract double cargo_ship();

    public abstract double cargo1();

    public abstract double cargo2();

    public abstract double cargo3();

    public abstract double hp_feed_jog();
}