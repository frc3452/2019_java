package frc.robot.util.drivers.controllers;

import java.util.function.Supplier;

import frc.robot.util.GZTimer;
import frc.robot.ConfigurableDrive.LatchedBoolean;

public class GZButton {
    private LatchedBoolean lb = new LatchedBoolean();

    private GZTimer mTimePressed = null;

    private Supplier<Boolean> supplier1;
    private Supplier<Boolean> supplier2;

    private Supplier<Boolean> main;

    public GZButton(DeepSpaceController controller, boolean useTimer, Supplier<Boolean> supplier1,
            Supplier<Boolean> supplier2) {
        this.supplier1 = supplier1;
        this.supplier2 = supplier2;

        if (useTimer)
            mTimePressed = new GZTimer();

        this.main = this.supplier1;
        controller.addButton(this);
    }

    public GZButton(DeepSpaceController controller, Supplier<Boolean> supplier1, Supplier<Boolean> supplier2) {
        this(controller, false, supplier1, supplier2);
    }

    public GZButton(DeepSpaceController controller, boolean useTimer, Supplier<Boolean> supplier1) {
        this(controller, useTimer, supplier1, null);
    }

    public GZButton(DeepSpaceController controller, Supplier<Boolean> supplier1) {
        this(controller, false, supplier1);
    }

    public void useSupplier1(boolean supplier1) {
        if (supplier2 == null)
            return;

        this.main = (supplier1 ? this.supplier1 : this.supplier2);
    }

    public boolean pressedFor(double seconds) {
        if (mTimePressed == null)
            return false;

        if (get())
            mTimePressed.startIfStopped();
        else
            mTimePressed.stopTimer();

        return mTimePressed.get() > seconds;
    }

    public boolean get() {
        return main.get();
    }

    public boolean updated() {
        return lb.update(main.get());
    }
}
