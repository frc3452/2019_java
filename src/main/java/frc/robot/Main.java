/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.poofs.util.math.Rotation2d;

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
        RobotBase.startRobot(Robot::new);
        // test();
    }

    public static void test() {
        // Rotation2d 

        // double d = new Rotation2d(0).nearestCardinalPlus().getNormalDegrees();
        // System.out.println(d);
        // for (double d = 0; d <= 360; d += 10)
        // {
        //     Rotation2d r = new Rotation2d(d).nearestCardinalPlus();
        //     System.out.println(d + "\t" + r.getNormalDegrees());
        // }
    }
}
