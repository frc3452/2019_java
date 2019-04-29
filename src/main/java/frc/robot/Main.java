/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.util.GZPrevious;
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
        // testGZPrev();
        print();
        // testRange();
        // RobotBase.startRobot(Robot::new);
    }

    private static void print()
    {
        System.out.println(GZUtil.getRandDouble(3, 4));
    }

    private static void testGZPrev() {
        GZPrevious<Double> prev = new GZPrevious<Double>(1.0);

        System.out.println(prev.update(1.0));
        System.out.println(prev.update(3.0));
        System.out.println(prev.update(3.0));
        System.out.println(prev.update(2.0));
        System.out.println(prev.update(1.0));
        System.out.println(prev.update(0.0));
    }

    private static void testRange() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("A");
        list.add("B");
        list.add("C");

        for (int i = -1; i < 5; i++) {
            System.out.println(i + " " + GZUtil.goodRange(i, list));
        }
    }
}
