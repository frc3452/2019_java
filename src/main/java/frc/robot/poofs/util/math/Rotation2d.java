package frc.robot.poofs.util.math;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import frc.robot.util.GZUtil;

import static frc.robot.util.GZUtil.kEpsilon;

/**
 * A rotation in a 2d coordinate frame represented a point on the unit circle
 * (cosine and sine).
 * <p>
 * Inspired by Sophus (https://github.com/strasdat/Sophus/tree/master/sophus)
 */
public class Rotation2d implements IRotation2d<Rotation2d> {
    protected static final Rotation2d kIdentity = new Rotation2d();

    public static final Rotation2d identity() {
        return kIdentity;
    }

    protected final double cos_angle_;
    protected final double sin_angle_;
    protected double theta_degrees = 0;
    protected double theta_radians = 0;

    public Rotation2d() {
        this(1, 0, false);
    }

    public Rotation2d(double x, double y, boolean normalize) {
        if (normalize) {
            // From trig, we know that sin^2 + cos^2 == 1, but as we do math on this object
            // we might accumulate rounding errors.
            // Normalizing forces us to re-scale the sin and cos to reset rounding errors.
            double magnitude = Math.hypot(x, y);
            if (magnitude > kEpsilon) {
                sin_angle_ = y / magnitude;
                cos_angle_ = x / magnitude;
            } else {
                sin_angle_ = 0;
                cos_angle_ = 1;
            }
        } else {
            cos_angle_ = x;
            sin_angle_ = y;
        }
        theta_degrees = Math.toDegrees(Math.atan2(sin_angle_, cos_angle_));
    }

    public int quadrant() {
        if (GZUtil.between(getNormalDegrees(), 0, 90)) {
            return 1;
        } else if (GZUtil.between(getNormalDegrees(), 90, 180)) {
            return 2;
        } else if (GZUtil.between(getNormalDegrees(), 180, 270)) {
            return 3;
        } else if (GZUtil.between(getNormalDegrees(), 270, 360)) {
            return 4;
        }

        return 0;
    }

    public Rotation2d(final Rotation2d other) {
        cos_angle_ = other.cos_angle_;
        sin_angle_ = other.sin_angle_;
        theta_degrees = Math.toDegrees(Math.atan2(sin_angle_, cos_angle_));
    }

    public Rotation2d(double theta_degrees) {
        cos_angle_ = Math.cos(Math.toRadians(theta_degrees));
        sin_angle_ = Math.sin(Math.toRadians(theta_degrees));
        this.theta_degrees = theta_degrees;
    }

    public Rotation2d(final Translation2d direction, boolean normalize) {
        this(direction.x(), direction.y(), normalize);
    }

    public static Rotation2d fromRadians(double angle_radians) {
        return new Rotation2d(Math.cos(angle_radians), Math.sin(angle_radians), false);
    }

    public static Rotation2d fromDegrees(double angle_degrees) {
        return new Rotation2d(angle_degrees);
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

    public double getUnboundedDegrees() {
        return theta_degrees;
    }

    /**
     * We can rotate this Rotation2d by adding together the effects of it and
     * another rotation.
     *
     * @param other The other rotation. See:
     *              https://en.wikipedia.org/wiki/Rotation_matrix
     * @return This rotation rotated by other.
     */
    public Rotation2d rotateBy(final Rotation2d other) {
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

    public boolean isParallel(final Rotation2d other) {
        return GZUtil.epsilonEquals(Translation2d.cross(toTranslation(), other.toTranslation()), 0.0);
    }

    public Translation2d toTranslation() {
        return new Translation2d(cos_angle_, sin_angle_);
    }

    /**
     * @return The pole nearest to this rotation.
     */
    public Rotation2d nearestPole() {
        double pole_sin = 0.0;
        double pole_cos = 0.0;
        if (Math.abs(cos_angle_) > Math.abs(sin_angle_)) {
            pole_cos = Math.signum(cos_angle_);
            pole_sin = 0.0;
        } else {
            pole_cos = 0.0;
            pole_sin = Math.signum(sin_angle_);
        }
        return new Rotation2d(pole_cos, pole_sin, false);
    }

    @Override
    public Rotation2d interpolate(final Rotation2d other, double x) {
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
        return "(" + fmt.format(getDegrees()) + " deg)";
    }

    @Override
    public String toCSV() {
        final DecimalFormat fmt = new DecimalFormat("#0.000");
        return fmt.format(getDegrees());
    }

    @Override
    public double distance(final Rotation2d other) {
        return inverse().rotateBy(other).getRadians();
    }

    public double distanceDeg(final Rotation2d other) {
        return Math.toDegrees(distance(other));
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null || !(other instanceof Rotation2d))
            return false;
        return distance((Rotation2d) other) < GZUtil.kEpsilon;
    }

    @Override
    public Rotation2d getRotation() {
        return this;
    }

    public double difference(Rotation2d other) {
        return difference(this.getDegrees(), other.getDegrees());
    }

    public static ArrayList<Rotation2d> getList(double... angles) {
        ArrayList<Rotation2d> ret = new ArrayList<>();
        for (double d : angles)
            ret.add(Rotation2d.fromDegrees(d));
        return ret;
    }

    public static ArrayList<Translation2d> getListToTranslations(ArrayList<Rotation2d> angles) {
        ArrayList<Translation2d> ret = new ArrayList<>();
        for (Rotation2d d : angles) {
            ret.add(d.toTranslation());
        }
        return ret;
    }

    public static ArrayList<Translation2d> getListToTranslations(double... angles) {
        ArrayList<Translation2d> ret = new ArrayList<>();
        for (double d : angles) {
            ret.add(new Rotation2d(d).toTranslation());
        }
        return ret;
    }

    public static ArrayList<Rotation2d> getCardinals() {
        return getList(0, 90, 180, 270);
    }

    public static ArrayList<Rotation2d> getCardinalsPlus() {
        return getList(0, 45, 90, 135, 180, 225, 270, 315);
    }

    public static ArrayList<Rotation2d> getInterCardinalds() {
        return getList(45, 135, 225, 315);
    }

    public static ArrayList<Rotation2d> rotateListBy(ArrayList<Rotation2d> list, Rotation2d rot) {
        ArrayList<Rotation2d> newList = new ArrayList<>();

        for (Rotation2d r : list) {
            newList.add(r.rotateBy(rot));
        }
        return newList;
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

    public static int closestGetPos(Rotation2d value, ArrayList<Rotation2d> rotations) {
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
        return pos;
    }

    public static int closestGetPos(Rotation2d value, Rotation2d other, ArrayList<Rotation2d> rotations) {
        double min = Double.MAX_VALUE;
        int pos = -1;
        List<Double> distances = new ArrayList<Double>();
        for (Rotation2d r : rotations) {
            double distanceTemp1 = Rotation2d.difference(value, r);
            double distanceTemp2 = Rotation2d.difference(other, r);
            distances.add(Math.min(distanceTemp1, distanceTemp2));
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
        return pos;
    }

    public static boolean between(Rotation2d value, Rotation2d lowBound, Rotation2d highBound) {
        double n = value.getNormalDegrees();
        double l = lowBound.getNormalDegrees();
        double h = highBound.getNormalDegrees();
        if (n >= l && n <= h)
            return true;
        return false;
    }

    public static boolean shouldTurnClockwise(Rotation2d current, Rotation2d target) {
        double tar = target.getNormalDegrees();
        double cur = current.getNormalDegrees();
        boolean cw;
        if (tar < 180) {
            if (GZUtil.between(cur, tar + 180, 360) || GZUtil.between(cur, 0, tar)) {
                cw = true;
            } else {
                cw = false;
            }
        } else {
            if (GZUtil.between(cur, tar, 360) || GZUtil.between(cur, 0, tar - 180)) {
                cw = false;
            } else {
                cw = true;
            }
        }
        return cw;
    }

    public boolean shouldTurnClockWiseToGetTo(Rotation2d target) {
        return shouldTurnClockwise(this, target);
    }

    public static double getNormalDegrees(double ang) {
        while (ang < 0) {
            ang += 360;
        }
        if (ang > 360) {
            ang = ang % 360;
        }
        return ang;
    }

    public double getNormalDegrees() {
        double ang = getDegrees();
        return getNormalDegrees(ang);
    }

    public Rotation2d print() {
        return print("");
    }

    public Rotation2d print(String message) {
        System.out.println(message + " " + this);
        return this;
    }
}