package frc.robot.util.requests;

public abstract class CheckRequest extends Request {

    public abstract void _act();

    @Override
    public void act() {
        if (!isFinished()) {
            _act();
        }
    }

}
