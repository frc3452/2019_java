package frc.robot.util.drivers.controllers;

import java.util.function.Supplier;

import frc.robot.util.LatchedBoolean;

public class GZButton {
    private LatchedBoolean lb = new LatchedBoolean();

    private Supplier<Boolean> supplier1;
    private Supplier<Boolean> supplier2;

    private Supplier<Boolean> main;

    public GZButton(DeepSpaceController controller, Supplier<Boolean> supplier1, Supplier<Boolean> supplier2) {
        this.supplier1 = supplier1;
        this.supplier2 = supplier2;

        this.main = this.supplier1;
        controller.addButton(this);
    }

    public GZButton(DeepSpaceController controller, Supplier<Boolean> supplier1) {
        this(controller, supplier1, null);
    }

    public void useSupplier1(boolean supplier1) {
        if (supplier2 == null)
            return;

        this.main = (supplier1 ? this.supplier1 : this.supplier2);
    }

    public boolean get() {
        return main.get();
    }

    public boolean updated() {
        return lb.update(main.get());
    }
}
