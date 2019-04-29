/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.ArrayList;

import frc.robot.ConfigurableDrive.ConfigurableDrive;
import frc.robot.ConfigurableDrive.ConfigurableDrive.Previous;
import frc.robot.util.GZUtil;

/**
 * Do NOT add any static variables to this class, or any initialization at all.
 * Unless you know what you are doing, do not modify this file except to change
 * the parameter class to the startRobot call.
 */
public final class Main {
    private Main() {
    }

    
    static double left = .3;
    static double right = .3;

    static boolean moveUpList = false;
    static boolean moveDownList = false;
    static boolean condition = true;
    private static void configDrive() {
        // GZJoystick joy = new GZJoystick(0);
        ConfigurableDrive drive = new ConfigurableDrive(() -> condition, () -> moveUpList, () -> moveDownList);

        drive.addTankDrive(() -> left, () -> right);
        drive.addArcadeDrive("Arcade", ()->left, ()->right);

        System.out.println(drive.update());
        
        left = .25;
        
        System.out.println(drive.update());
        
        moveUpList = true;
        System.out.println(drive.update());
        moveUpList = false;
        System.out.println(drive.update());
        moveUpList = true;
        System.out.println(drive.update());

        condition = false;

        moveDownList = true;
        System.out.println(drive.update());

        condition = true;

        moveDownList = false;
        System.out.println(drive.update());
        moveDownList = true;
        System.out.println(drive.update());

    }



    public static void main(String... aArgs) {
        // testGZPrev();
        // print();
        // testRange();
        configDrive();
        // RobotBase.startRobot(Robot::new);
    }

    private static void print() {
        System.out.println(GZUtil.getRandDouble(3, 4));
    }

    private static void testGZPrev() {
        Previous<Double> prev = new Previous<Double>(1.0);

        System.out.println(prev.update(1.0));
        System.out.println(prev.update(prev.getPrev() + 1));
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
