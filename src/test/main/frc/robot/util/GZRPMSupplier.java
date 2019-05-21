package frc.robot.util;

import java.util.function.Supplier;

import frc.robot.util.drivers.IGZHardware;
import frc.robot.util.drivers.motorcontrollers.GZSRX;

public class GZRPMSupplier implements IGZHardware {

    private final Supplier<Double> mSupplier;
    private final GZSRX mTalon;

    public GZRPMSupplier(GZSRX talon, Supplier<Double> supplier)
    {
        this.mTalon = talon;
        this.mSupplier = supplier;
    }

    public Supplier<Double> getSupplier()
    {
        return mSupplier;
    }

    @Override
    public String getGZName() {
        return this.mTalon.getGZName();
    }




}