package frc.robot.auto.commands.functions.drive.pathfollowing;

import java.util.ArrayList;

import frc.robot.Constants.kDrivetrain;
import frc.robot.Constants.kPathFollowing;
import frc.robot.auto.commands.AutoModeBuilder.AutoMovement;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.fieldprofiles.FieldProfile;
import frc.robot.poofs.util.control.Path;
import frc.robot.poofs.util.control.PathFollower;
import frc.robot.poofs.util.math.RigidTransform2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.util.GZPID.GZPIDPair;
import frc.robot.util.GZUtil;

public abstract class PathContainer {

    public ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();

    private boolean mOdometryNeedsZeroed = false;

    public PathContainer needsZeroed()
    {
        mOdometryNeedsZeroed = true;
        return this;
    }

    public boolean doesNeedZero()
    {
        return mOdometryNeedsZeroed;
    }

    public static PathContainer getReversed(PathContainer other) {
        PathContainer ret = new PathContainer() {
            @Override
            public boolean isReversed() {
                return !other.isReversed();
            }

            @Override
            public Rotation2d getStartRotation() {
                return other.getEndRotation();
            }

            @Override
            public String toString() {
                return other.toString() + " REVERSED";
            }

            // @Override
            // public Rotation2d getStartGyroMovement() {
            // return other.getStartGyroMovement();
            // }

            // @Override
            // public Rotation2d getEndGyroMovement() {
            // return other.getEndGyroMovement();
            // }
        };

        ArrayList<Waypoint> flippedPoints = new ArrayList<Waypoint>();

        // Flip waypoints
        for (int i = other.sWaypoints.size() - 1; i >= 0; i--) {
            flippedPoints.add(new Waypoint(other.sWaypoints.get(i)));
        }

        double minSpeed = 3452;
        // Find slowest nonzero speed
        for (Waypoint p : other.sWaypoints) {
            if (p.speed != 0 && p.speed <= minSpeed) {
                minSpeed = p.speed;
            }
        }

        if (minSpeed == 3452) {
            System.out.println("ERROR Could not find nonzero speed for path " + other.getClass().getSimpleName());
            return null;
        }

        // Apply speed
        for (int i = 0; i < flippedPoints.size(); i++) {
            flippedPoints.get(i).speed = minSpeed;
        }
        ret.sWaypoints = flippedPoints;
        return ret;
    }

    public Rotation2d getStartGyroMovement() {
        return null;
    }

    public Rotation2d getEndGyroMovement() {
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public PathContainer getReversed() {
        return getReversed(this);
    }

    public static PathContainer getFlipped(PathContainer other) {
        PathContainer ret = new PathContainer() {
            @Override
            public boolean isReversed() {
                return other.isReversed();
            }

            public boolean isLeftPath() {
                return !other.isLeftPath();
            }

            @Override
            public String toString() {
                return other.toString() + " FLIPPED";
            }

            @Override
            public Rotation2d getStartGyroMovement() {
                Rotation2d rot = other.getStartGyroMovement();
                Rotation2d ret = null;

                if (rot != null)
                    ret = rot.inverse();

                return rot;
            }

            @Override
            public Rotation2d getEndGyroMovement() {
                Rotation2d rot = other.getEndGyroMovement();
                Rotation2d ret = null;

                if (rot != null)
                    ret = rot.inverse();

                return rot;
            }
        };

        for (Waypoint p : other.sWaypoints) {
            Waypoint newPoint = new Waypoint(p);
            newPoint.position.setY(FieldProfile.centerLineY + (FieldProfile.centerLineY - p.position.y()));
            ret.sWaypoints.add(newPoint);
        }

        return ret;
    }

    public PathFollower.Parameters getParameters() {
        return kPathFollowing.pathFollowingConstants;
    }

    public PathContainer getFlipped() {
        return getFlipped(this);
    }



    public PathContainer getLeft() {
        if (this.isLeftPath())
            return this;

        return getFlipped();
    }

    public PathContainer getRight() {
        if (!this.isLeftPath()) {
            return this;
        }
        return getFlipped();
    }

    public PathContainer get(boolean left) {
        if (left)
            return getLeft();

        return getRight();
    }

    public AutoMovement m()
    {
        return new AutoMovement(this);
    }

    public boolean isLeftPath() {
        return true;
    }

    public abstract boolean isReversed();

    public GZPIDPair getPID() {
        return kDrivetrain.PID;
    }

    public Path buildPath() {
        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    public Rotation2d getStartRotation() {
        return GZUtil.angleBetweenPoints(sWaypoints.get(0), sWaypoints.get(1))
                .rotateBy(Rotation2d.fromDegrees(isReversed() ? 180 : 0));
    }

    public Rotation2d getEndRotation() {
        final int last = sWaypoints.size() - 1;
        return GZUtil.angleBetweenPoints(sWaypoints.get(last), sWaypoints.get(last - 1))
                .rotateBy(Rotation2d.fromDegrees(isReversed() ? 180 : 0));
    }

    public RigidTransform2d getStartPose() {
        final Waypoint firstPoint = sWaypoints.get(0);

        return new RigidTransform2d(firstPoint.position, getStartRotation());
    }

    public RigidTransform2d getEndPose() {
        final Waypoint lastPoint = sWaypoints.get(sWaypoints.size() - 1);

        return new RigidTransform2d(lastPoint.position, getEndRotation());
    }

    public ArrayList<PathContainer> toList() {
        ArrayList<PathContainer> ret = new ArrayList<PathContainer>();
        ret.add(this);
        return ret;
    }

    public PathContainer print() {
        System.out.println("PRINTING PATH  " + this.getClass().getSimpleName());
        System.out.println("Reversed: " + this.isReversed());

        // TODO Can we figure out end angle without building path? Is this causing the
        // problem?
        System.out.println("Starting position: " + this.getStartPose());
        System.out.println("Ending position: " + this.getEndPose());

        System.out.println("|X-Y| |Radius| |Speed|");
        int counter = 1;
        for (Waypoint o : sWaypoints) {
            System.out
                    .println("Waypoint " + counter++ + " :" + o.position.toString() + "\t" + o.radius + "\t" + o.speed);
        }

        // System.out.println(this.buildPath().toString());

        System.out.println("\n");
        return this;
    }

}
