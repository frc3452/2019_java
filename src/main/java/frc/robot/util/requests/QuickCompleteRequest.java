package frc.robot.util.requests;

public abstract class QuickCompleteRequest {

    boolean quickComplete;

    public QuickCompleteRequest(boolean quickComplete) {
        this.quickComplete = quickComplete;
    }

    public abstract void _act();

    public abstract boolean _isFinished();

    public Request generate() {
        if (quickComplete) {
            return new Request() {

                @Override
                public void act() {
                    _act();
                }
            };
        } else {
            return new Request() {

                @Override
                public void act() {
                    _act();
                }

                @Override
                public boolean isFinished() {
                    return _isFinished();
                }
            };
        }
    }

}