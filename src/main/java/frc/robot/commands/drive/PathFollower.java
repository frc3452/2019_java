package frc.robot.commands.drive;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drive;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class PathFollower extends Command {

    private Drive drive = Drive.getInstance();

    private int mPos = 0;
    private Trajectory mLeft, mRight;

    public PathFollower() {
    }

    private Notifier mNotifier = new Notifier(new Runnable(){
    
        @Override
        public void run() {

            double left, right;

            // drive.setVelocity(left, right);
        }
    });


    protected void initialize() {

        {
            Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC,
                    Trajectory.Config.SAMPLES_HIGH, 0.05, 1.7, 2.0, 60.0);
            Waypoint[] points = new Waypoint[] { new Waypoint(0, 0, 0), new Waypoint(10, 0, 0), };

            Trajectory trajectory = Pathfinder.generate(points, config);

            // Wheelbase Width = 0.5m
            TankModifier modifier = new TankModifier(trajectory).modify(0.5);

            // Do something with the new Trajectories...
            mLeft = modifier.getLeftTrajectory();
            mRight = modifier.getRightTrajectory();
        }
    }

    protected void execute() {
    }

    protected void end() {
        drive.stop();
    }

    protected void interrupted() {
        end();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}