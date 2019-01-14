package frc.robot.util;

public abstract class SuperstructureComponent {
    private boolean beingUsedManually = false;

    public SuperstructureComponent() {
    }


    public void setComponent(double value, boolean manual)
    {
        setComponent(value, value, manual);
    }

    public void setComponent(double value)
    {
        setComponent(value, value);
    }

    public void setComponent(double value, double value2)
    {
        setComponent(value, value2, false);
    }

    public void setComponent(double value, double value2, boolean manual) {
        if (manual)
            this.beingUsedManually = true;

        if (!this.beingUsedManually || ((this.beingUsedManually && manual)))
            set(value, value2);
    }

    public void notBeingUsedManually()
    {
        this.beingUsedManually = false;
    }

    public abstract void set(double value, double value2);

}