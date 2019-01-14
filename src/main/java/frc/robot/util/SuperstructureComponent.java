package frc.robot.util;

public abstract class SuperstructureComponent {
    private boolean beingUsedManually = false;

    public SuperstructureComponent() {
    }

    public void setComponent(double value) {
        setComponent(value, false);
    }

    public void setComponent(double value, boolean manual) {
        if (manual)
            this.beingUsedManually = true;

        if (!this.beingUsedManually || ((this.beingUsedManually && manual)))
            set(value);
    }

    public void notBeingUsedManually()
    {
        this.beingUsedManually = false;
    }

    public abstract void set(double value);

}