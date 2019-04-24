/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.util.GZUtil;

/**
 * Do NOT add any static variables to this class, or any initialization at all.
 * Unless you know what you are doing, do not modify this file except to change
 * the parameter class to the startRobot call.
 */
public final class Main {
    private Main() {
    }

    /**
     * Main initialization function. Do not perform any initialization here.
     *
     * <p>
     * If you change your main robot class, change the parameter type.
     */
    public static void main(String... aArgs) {
        // angle();
        // fallThrough();
        // closestTurn();
        RobotBase.startRobot(Robot::new);
    }

    enum Test {
        A, B, C
    }

    private static void fallThrough() {
        fallThrough(Test.A);
        fallThrough(Test.B);
        fallThrough(Test.C);
    }

    private static void fallThrough(Test var) {
        switch (var) {
        case A:
            System.out.println("it's A");
            break;
        case B:
        case C:
            System.out.println("it's b or C!");
            break;
        }
    }

    private static void angle() {
        System.out.println(Rotation2d.between(Rotation2d.fromDegrees(271), Rotation2d.fromDegrees(270),
                Rotation2d.fromDegrees(1)));
    }

    private static void insideAngle() {

        boolean success = false;
        for (int i = 0; i < 5 || !success; i++) {
            Rotation2d start = Rotation2d.fromDegrees(GZUtil.round(GZUtil.getRandDouble(0, 360), 0));
            double areaAround = GZUtil.round(GZUtil.getRandDouble(40, 50), 0);

            Rotation2d bound1 = start.rotateBy(Rotation2d.fromDegrees(-areaAround));
            Rotation2d bound2 = start.rotateBy(Rotation2d.fromDegrees(areaAround));
            System.out.println("Point at " + start.getNormalDegrees());
            System.out.println("Area around: " + areaAround);
            System.out.println("Bound 1: " + bound1.getNormalDegrees());
            System.out.println("Bound 2: " + bound2.getNormalDegrees());

            Rotation2d randPoint = Rotation2d.fromDegrees(GZUtil.getRandDouble(0, 360));

            System.out.println("Random point: " + randPoint.getNormalDegrees());

            boolean inside = Rotation2d.between(randPoint, bound1, bound2);
            System.out.println("Point inside range: " + inside);

            if (inside)
                success = true;

            System.out.println("\n\n");
        }
    }

    private static void closestTurn() {
        for (int i = 0; i < 5; i++) {
            Rotation2d start = Rotation2d.fromDegrees(GZUtil.round(GZUtil.getRandDouble(0, 360), 0));
            Rotation2d target = Rotation2d.fromDegrees(GZUtil.round(GZUtil.getRandDouble(0, 360), 0));
            System.out.println("Starting at " + start.getNormalDegrees());
            System.out.println("Target at " + target.getNormalDegrees());

            System.out.println("Clockwise turn: " + start.shouldTurnClockWiseToGetTo(target));

            System.out.println("\n\n");
        }
    }
}
