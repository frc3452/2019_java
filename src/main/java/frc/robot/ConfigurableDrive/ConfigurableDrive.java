package frc.robot.ConfigurableDrive;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * This configurable drive controller was written as a senior project by Max
 * Dreher from FRC Team GreengineerZ (#3452)
 * 
 * Designed to be drop in and configurable,
 */
public class ConfigurableDrive {

    private Button s_upTick;
    private Button s_downTick;

    private ArrayList<DriveStyle> mStyles = new ArrayList<DriveStyle>();

    public ConfigurableDrive() {
    }

    public abstract static class DriveStyle {
        private final String name;
        private Supplier<Double>[] axises;

        /**
         * 
         * @param axises
         */
        public DriveStyle(String name, Supplier<Double>... axises) {
            this.axises = axises;
            this.name = name;
        }

        /**
         * First axis passed will be axis 1, NOT 0
         * 
         * @param axis
         * @return
         */
        public double getAxis(int axis) {
            if (axis < 1) {
                throwError("Axis " + axis + " less than 1; first axis starts at 1!");
                return 0;
            } 

            if (axis > 3);
        
            return axises[axis - 1].get();
        }

        private void throwError(String message)
        {
            System.out.println("ERROR ConfigurableDrive option [" + toString() + "]" + message);
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public static class Button {
        private LatchedBoolean mLB = new LatchedBoolean();
        private Supplier<Boolean> supplier;

        public Button(Supplier<Boolean> supplier) {
            this.supplier = supplier;
        }

        public boolean get() {
            return supplier.get();
        }

        public boolean updated() {
            return mLB.update(get());
        }
    }

}