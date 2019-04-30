package frc.robot.poofs.util.math;

import static frc.robot.poofs.util.Util.epsilonEquals;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import frc.robot.poofs.util.Interpolable;

/**
 * A rotation in a 2d coordinate frame represented a point on the unit circle
 * (cosine and sine).
 * 
 * Inspired by Sophus (https://github.com/strasdat/Sophus/tree/master/sophus)
 * 
 * 
 * 
 * 
 * https://www.desmos.com/calculator/vekweuq0gj
 */
public class Rotation2d implements Interpolable<Rotation2d> {
    protected static final Rotation2d kIdentity = new Rotation2d();

    public static final Rotation2d identity() {
        return kIdentity;
    }

    protected static final double kEpsilon = 1E-9;

    protected double cos_angle_;
    protected double sin_angle_;

    public Rotation2d() {
        this(1, 0, false);
    }

    public Rotation2d(double x, double y, boolean normalize) {
        cos_angle_ = x;
        sin_angle_ = y;
        if (normalize) {
            normalize();
        }
    }

    public static ArrayList<Rotation2d> getList(double... angles) {
        ArrayList<Rotation2d> ret = new ArrayList<Rotation2d>();

        for (double d : angles)
            ret.add(Rotation2d.fromDegrees(d));

        return ret;
    }

    public static ArrayList<Rotation2d> getCardinals() {
        return getList(0, 90, 180, 270);
    }

    public static ArrayList<Rotation2d> getCardinalsPlus() {
        return getList(0, 45, 90, 135, 180, 225, 270, 315);
    }

    public Rotation2d(Rotation2d other) {
        cos_angle_ = other.cos_angle_;
        sin_angle_ = other.sin_angle_;
    }

    public Rotation2d(Translation2d direction, boolean normalize) {
        this(direction.x(), direction.y(), normalize);
    }

    public static Rotation2d fromRadians(double angle_radians) {
        return new Rotation2d(Math.cos(angle_radians), Math.sin(angle_radians), false);
    }

    public static Rotation2d fromDegrees(double angle_degrees) {
        return fromRadians(Math.toRadians(angle_degrees));
    }

    public double difference(Rotation2d other) {
        return difference(this.getDegrees(), other.getDegrees());
    }

    public static double difference(Rotation2d a1, Rotation2d a2) {
        return difference(a1.getDegrees(), a2.getDegrees());
    }

    public static double difference(double a1, double a2) {
        double angle = 180 - Math.abs(Math.abs(a1 - a2) - 180);
        return angle;
    }

    public static Rotation2d closestCoordinatePlus(Rotation2d value) {
        return closest(value, getCardinalsPlus());
    }

    public static Rotation2d closest(Rotation2d value, ArrayList<Rotation2d> rotations) {
        double min = Double.MAX_VALUE;
        int pos = -1;
        List<Double> distances = new ArrayList<Double>();

        for (Rotation2d r : rotations) {
            double distanceTemp = Rotation2d.difference(value, r);
            distances.add(distanceTemp);
        }

        {
            int counter = 0;
            for (Double d : distances) {
                if (d < min) {
                    min = d;
                    pos = counter;
                }
                counter++;
            }
        }

        return rotations.get(pos);
    }

    public boolean equals(Rotation2d other) {
        if (this.cos_angle_ == other.cos_angle_ && this.sin_angle_ == other.sin_angle_)
            return true;
        return false;
    }

    /**
     * From trig, we know that sin^2 + cos^2 == 1, but as we do math on this object
     * we might accumulate rounding errors. Normalizing forces us to re-scale the
     * sin and cos to reset rounding errors.
     */
    public void normalize() {
        double magnitude = Math.hypot(cos_angle_, sin_angle_);
        if (magnitude > kEpsilon) {
            sin_angle_ /= magnitude;
            cos_angle_ /= magnitude;
        } else {
            sin_angle_ = 0;
            cos_angle_ = 1;
        }
    }

    public double cos() {
        return cos_angle_;
    }

    public double sin() {
        return sin_angle_;
    }

    public double tan() {
        if (Math.abs(cos_angle_) < kEpsilon) {
            if (sin_angle_ >= 0.0) {
                return Double.POSITIVE_INFINITY;
            } else {
                return Double.NEGATIVE_INFINITY;
            }
        }
        return sin_angle_ / cos_angle_;
    }

    public double getRadians() {
        return Math.atan2(sin_angle_, cos_angle_);
    }

    public double getDegrees() {
        return Math.toDegrees(getRadians());
    }

    public static boolean between(Rotation2d value, Rotation2d lowBound, Rotation2d highBound) {
        double lowest = Math.min(lowBound.getNormalDegrees(), highBound.getNormalDegrees());
        double highest = Math.max(lowBound.getNormalDegrees(), highBound.getDegrees());
        return between(value.getNormalDegrees(), lowest, highest);
    }

    public static boolean between(double value, double low, double high) {
        if (value >= low && value <= high)
            return true;
        return false;
    }

    public static boolean shouldTurnClockwise(Rotation2d current, Rotation2d target) {
        double tar = target.getNormalDegrees();

        boolean cw;
        String out = target.getNormalDegrees() + "\t" + current.getNormalDegrees();

        if (tar > 180) {
            if (between(current, Rotation2d.fromDegrees(tar - 180), target)) {
                cw = true;
                out += " case 1";
            } else {
                out += " case 2";
                cw = false;
            }
        } else {
            if (between(current, target, Rotation2d.fromDegrees(tar + 180))) {
                out += " case 3";
                cw = false;
            } else {
                out += " case 4";
                cw = true;
            }
        }

        out += cw;
        System.out.println(out);

        return cw;
    }

    public boolean shouldTurnClockWiseToGetTo(Rotation2d target) {
        return shouldTurnClockwise(this, target);
    }

    public double getNormalDegrees() {
        double ang = getDegrees();
        while (ang < 0) {
            ang += 360;
        }

        if (ang > 360) {
            ang = ang % 360;
        }

        // ang = GZUtil.roundTo(ang, 3);

        return ang;
    }

    /**
     * We can rotate this Rotation2d by adding together the effects of it and
     * another rotation.
     * 
     * @param other The other rotation. See:
     *              https://en.wikipedia.org/wiki/Rotation_matrix
     * @return This rotation rotated by other.
     */
    public Rotation2d rotateBy(Rotation2d other) {
        return new Rotation2d(cos_angle_ * other.cos_angle_ - sin_angle_ * other.sin_angle_,
                cos_angle_ * other.sin_angle_ + sin_angle_ * other.cos_angle_, true);
    }

    public Rotation2d normal() {
        return new Rotation2d(-sin_angle_, cos_angle_, false);
    }

    /**
     * The inverse of a Rotation2d "undoes" the effect of this rotation.
     * 
     * @return The opposite of this rotation.
     */
    public Rotation2d inverse() {
        return new Rotation2d(cos_angle_, -sin_angle_, false);
    }

    /**
     * Takes an angle CCW -> CW or CW -> CCW
     */
    public Rotation2d flipDirection() {
        return new Rotation2d(-cos_angle_, -sin_angle_, false);
    }

    public boolean isParallel(Rotation2d other) {
        return epsilonEquals(Translation2d.cross(toTranslation(), other.toTranslation()), 0.0, kEpsilon);
    }

    public Translation2d toTranslation() {
        return new Translation2d(cos_angle_, sin_angle_);
    }

    @Override
    public Rotation2d interpolate(Rotation2d other, double x) {
        if (x <= 0) {
            return new Rotation2d(this);
        } else if (x >= 1) {
            return new Rotation2d(other);
        }
        double angle_diff = inverse().rotateBy(other).getRadians();
        return this.rotateBy(Rotation2d.fromRadians(angle_diff * x));
    }

    @Override
    public String toString() {
        final DecimalFormat fmt = new DecimalFormat("#0.000");
        return "(" + fmt.format(getDegrees()) + " deg)" + "\tNormalized (" + fmt.format(getNormalDegrees()) + ")";
    }
}
