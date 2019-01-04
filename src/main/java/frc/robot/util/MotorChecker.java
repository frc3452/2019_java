package frc.robot.util;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants.kFiles;
import frc.robot.Robot;
import frc.robot.util.GZFileMaker.ValidFileExtension;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.GZFiles.HTML;
import frc.robot.util.drivers.GZSpeedController;

public class MotorChecker {
    public static class AmperageChecker {
        public static class Current {
            private final double mCurrent;
            private boolean mFail = false;

            public Current(double current) {
                this.mCurrent = current;
            }

            public void setFail() {
                this.mFail = true;
            }

            public boolean getFail() {
                return this.mFail;
            }

            public Double getCurrent() {
                return this.mCurrent;
            }
        }

        public static class MotorTestingGroup {
            private String mName;
            private double averageForwardAmperage;
            private double averageReverseAmperage;

            private GZSubsystem mSubsystem;

            private List<GZSpeedController> controllers = new ArrayList<GZSpeedController>();
            private ArrayList<Current> forwardCurrents;
            private ArrayList<Current> reverseCurrents;

            private CheckerConfig mCheckerConfig;

            public MotorTestingGroup(GZSubsystem subsystem, String name, List<GZSpeedController> controllers,
                    CheckerConfig config) {
                this.controllers = controllers;
                this.mName = name;
                this.mSubsystem = subsystem;
                this.mCheckerConfig = config;

                clearGroup();
            }

            public boolean hasFail() {
                boolean hasFail = false;

                for (Current c : forwardCurrents)
                    hasFail |= c.getFail();

                for (Current c : reverseCurrents)
                    hasFail |= c.getFail();

                return hasFail;
            }

            public Double getAverageForwardAmperage() {
                return averageForwardAmperage;
            }

            public void setAverageForwardAmperage(double averageForwardAmperage) {
                this.averageForwardAmperage = averageForwardAmperage;
            }

            public Double getAverageReverseAmperage() {
                return averageReverseAmperage;
            }

            public void setAverageReverseAmperage(double averageReverseAmperage) {
                this.averageReverseAmperage = averageReverseAmperage;
            }

            public CheckerConfig getConfig() {
                return this.mCheckerConfig;
            }

            public String getName() {
                return this.mName;
            }

            public GZSubsystem getSubsystem() {
                return this.mSubsystem;
            }

            public List<GZSpeedController> getControllers() {
                return controllers;
            }

            public ArrayList<Current> getForwardCurrents() {
                return forwardCurrents;
            }

            public ArrayList<Current> getReverseCurrents() {
                return reverseCurrents;
            }

            public void clearGroup() {
                forwardCurrents = new ArrayList<Current>();
                reverseCurrents = new ArrayList<Current>();
            }

        }

        public static class CheckerConfig {
            public static List<MotorTestingGroup> getFromFile(GZSubsystem subsystem) {
                File file = kFiles.MOTOR_TESTING_CONFIG.getFile();

                List<MotorTestingGroup> ret = new ArrayList<MotorTestingGroup>();
                try {

                    Scanner scnr = new Scanner(new FileReader(file));

                    // Skip first line of headers
                    scnr.nextLine();

                    while (scnr.hasNext()) {
                        String t = scnr.nextLine();
                        String split[] = t.split(",");

                        if (split[0].equals(subsystem.toString())) {
                            List<GZSpeedController> controllers = new ArrayList<GZSpeedController>();

                            double currentFloor = Double.valueOf(split[2]);
                            double currentEpsilon = Double.valueOf(split[3]);
                            double runTimeSec = Double.valueOf(split[4]);
                            double waitTimeSec = Double.valueOf(split[5]);
                            double outputPercentage = Double.valueOf(split[6]);
                            boolean reverseAfterGroup = Boolean.valueOf(split[7]);

                            CheckerConfig config = new CheckerConfig(currentFloor, currentEpsilon, runTimeSec,
                                    waitTimeSec, outputPercentage, reverseAfterGroup);

                            for (int i = 8; i < split.length; i++) {
                                for (GZSpeedController controller : subsystem.mTalons.values())
                                    if (controller.getGZName().equals(split[i]))
                                        controllers.add(controller);

                                for (GZSpeedController controller : subsystem.mDumbControllers.values())
                                    if (controller.getGZName().equals(split[i]))
                                        controllers.add(controller);
                            }
                            ret.add(new MotorTestingGroup(subsystem, split[1], controllers, config));
                        }
                    }
                    scnr.close();

                    if (ret.isEmpty())
                        return null;
                    return ret;
                } catch (Exception e) {
                    // on error
                    System.out.println("ERROR Could not load MotorTestingConfig for " + subsystem.toString() + " at "
                            + file.toPath());
                            // e.printStackTrace();
                    return null;
                }
            }

            public double mCurrentFloor = 0;
            public double mCurrentEpsilon = 0;

            public double mRunTimeSec = 0;
            public double mWaitTimeSec = 0;
            public double mRunOutputPercentage = 0.0;

            public boolean mReverseAfterGroup = true;

            public CheckerConfig(double currentFloor, double currentEpsilon, double runTimeSec, double waitTimeSec,
                    double outputPercentage, boolean reverseAfterGroup) {
                this.mCurrentFloor = currentFloor;
                this.mCurrentEpsilon = currentEpsilon;
                this.mRunTimeSec = runTimeSec;
                this.mWaitTimeSec = waitTimeSec;
                this.mRunOutputPercentage = outputPercentage;
                this.mReverseAfterGroup = reverseAfterGroup;
            }

            public String configAsString() {
                String retval = "";

                retval += "Current floor: " + mCurrentFloor + "\n";
                retval += "Current epsilon: " + mCurrentEpsilon + "\n";
                retval += "Run time (seconds): " + mRunTimeSec + "\n";
                retval += "Wait time (seconds): " + mWaitTimeSec + "\n";
                retval += "Output percentage: " + mRunOutputPercentage + "\n";
                retval += "Reverse after group: " + mReverseAfterGroup + "\n";

                return retval;
            }

            public void print() {
                System.out.println(toString());
            }
        }

        private static AmperageChecker mInstance = null;

        public static AmperageChecker getInstance() {
            if (mInstance == null)
                mInstance = new AmperageChecker();
            return mInstance;
        }

        private AmperageChecker() {
            clearValues();
        }

        private Map<GZSubsystem, ArrayList<MotorTestingGroup>> subsystemMap;

        private double mTimeNeeded = 0;

        public void clearValues() {
            subsystemMap = new HashMap<GZSubsystem, ArrayList<MotorTestingGroup>>();
        }

        public void addTalonGroup(MotorTestingGroup group) {
            if (group.getConfig() == null || group == null) {
                return;
            }

            if (!subsystemMap.containsKey(group.getSubsystem()))
                subsystemMap.put(group.getSubsystem(), new ArrayList<MotorTestingGroup>());

            subsystemMap.get(group.getSubsystem()).add(group);
        }

        public void addTalonGroups(List<MotorTestingGroup> multipleGroups)
        {
            if (multipleGroups == null)
                return;

            for (MotorTestingGroup g : multipleGroups)
                if (g != null)
                    addTalonGroup(g);
        }

        public void checkMotors() {
            boolean failure = false;

            // Clear all fails
            for (GZSubsystem s : subsystemMap.keySet())
                s.clearMotorTestingFails();

            mTimeNeeded = 0;
            for (ArrayList<MotorTestingGroup> allGroups : subsystemMap.values()) {
                for (MotorTestingGroup group : allGroups) {
                    mTimeNeeded += group.getConfig().mRunTimeSec * 2 * group.controllers.size();
                    mTimeNeeded += group.getConfig().mWaitTimeSec * 2 * group.controllers.size();
                }
            }

            System.out.println("Starting amperage checker... estimated time needed: " + mTimeNeeded + " seconds");

            // will store the current motor groups
            ArrayList<MotorTestingGroup> talonGroups;

            // loop through every subsystem
            for (GZSubsystem s : subsystemMap.keySet()) {
                // get current groups
                talonGroups = subsystemMap.get(s);

                // loop through each group in this subsystem
                for (MotorTestingGroup group : talonGroups) {
                    // empty group on startup
                    group.clearGroup();

                    // Controllers
                    List<GZSpeedController> controllersToCheck = group.getControllers();

                    System.out.println("Checking subsystem " + group.getSubsystem().toString() + " group "
                            + group.getName() + " for " + controllersToCheck.size()
                            + " controllers. Estimated total time left: " + mTimeNeeded + " seconds");

                    // Dont allow other methods to control these controllers
                    for (GZSpeedController t : controllersToCheck)
                        t.lockOutController(true);

                    // Loop through controllers (running all controllers in group forwards, then all
                    // backwards)
                    if (group.getConfig().mReverseAfterGroup) {
                        // start forward
                        boolean forward = true;

                        // do this twice
                        for (int i = 0; i < 2; i++) {

                            // Check each talon
                            for (GZSpeedController individualTalonToCheck : controllersToCheck) {
                                failure |= checkController(individualTalonToCheck, group, forward);
                            }
                            // once we've checked them all, do it again but the other way
                            forward = !forward;
                        }
                    } else { // test each talon forwards, then test it backwards, then move onto the next
                             // talon
                        for (GZSpeedController individualControllerToCheck : controllersToCheck) {
                            // Test twice
                            for (int i = 0; i < 2; i++)
                                failure |= checkController(individualControllerToCheck, group, (i == 0));
                            // on the first loop when i == 0, go forwards, then go backwards
                        }
                    }

                    // We've now checked every current and recorded, run average checks

                    // Now run aggregate checks.
                    for (int i = 0; i < 2; i++) {
                        Double average = 0.0;
                        ArrayList<Current> currents;

                        // First loop, test forward currents
                        if (i == 0)
                            currents = group.getForwardCurrents();
                        else
                            currents = group.getReverseCurrents();

                        // Accumulate average
                        for (Current c : currents)
                            average += c.getCurrent();
                        average /= currents.size();

                        // Set average for groups
                        if (i == 0)
                            group.setAverageForwardAmperage(average);
                        else
                            group.setAverageReverseAmperage(average);

                        // Current is too far away from average
                        for (Current c : currents) {
                            if (!GZUtil.epsilonEquals(c.getCurrent(), average, group.getConfig().mCurrentEpsilon)) {
                                failure = true;
                                c.setFail();
                                group.getSubsystem().setMotorTestingFail();
                            }
                        }
                    }

                    // Unlock talons so another method can control them
                    for (GZSpeedController t : controllersToCheck)
                        t.lockOutController(false);
                }

            }

            System.out.println((failure ? "WARNING " : "") + "Amperage check " + (failure ? "failed!" : "passed!"));
            createHTMLFile();
        }

        private boolean checkController(GZSpeedController individualControllerToCheck, MotorTestingGroup group,
                boolean forward) {
            boolean failure = false;
            System.out.println(
                    "Checking: " + individualControllerToCheck.getGZName() + (forward ? " forwards" : " reverse"));

            ArrayList<Current> currents = (forward ? group.getForwardCurrents() : group.getReverseCurrents());

            individualControllerToCheck.set(group.getConfig().mRunOutputPercentage * (forward ? 1 : -1), true);
            Timer.delay(group.getConfig().mRunTimeSec);
            mTimeNeeded -= group.getConfig().mRunTimeSec;

            // Now poll the interesting information.
            double current = individualControllerToCheck.getAmperage();
            currents.add(new Current(current));

            individualControllerToCheck.set(0.0, true);

            // Perform individual check if current too low
            if (current < group.getConfig().mCurrentFloor) {
                currents.get(currents.size() - 1).setFail();
                // System.out.println(individualTalonToCheck.getGZName() + " has failed current
                // floor check vs "
                // + group.getConfig().mCurrentFloor + "!!");
                failure = true;
                group.getSubsystem().setMotorTestingFail();
            }
            Timer.delay(group.getConfig().mWaitTimeSec);
            mTimeNeeded -= group.getConfig().mWaitTimeSec;

            return failure;
        }

        private void createHTMLFile() {
            String body = "";

            // Time at top of file
            body += HTML.paragraph("Created on " + GZUtil.dateTime(false));

            // Loop through every subsystem
            for (GZSubsystem subsystem : subsystemMap.keySet()) {

                // groups for subsystem
                ArrayList<MotorTestingGroup> talonGroups = subsystemMap.get(subsystem);

                // header (write subsystem color in red if has error)
                String subsystemColor = subsystem.hasMotorTestingFail() ? "red" : "black";
                body += HTML.header(subsystem.toString(), 1, subsystemColor);

                // Content of entire subsystem
                String subsystemContent = "";

                for (MotorTestingGroup talonGroup : talonGroups) {

                    // values for the group we are currently creating
                    List<GZSpeedController> mtr = talonGroup.getControllers();
                    ArrayList<Current> fwd = talonGroup.getForwardCurrents();
                    ArrayList<Current> rev = talonGroup.getReverseCurrents();

                    // Sizes
                    int talonSize = mtr.size() - 1;
                    int fwdSize = fwd.size() - 1;
                    int revSize = rev.size() - 1;

                    // check size
                    if (!((talonSize == fwdSize) && (fwdSize == revSize))) {
                        System.out.println("Motor size (" + talonSize + ") and forward currents size (" + fwdSize
                                + ") and reverse currents size (" + revSize + ") not equal!");
                        return;
                    }

                    // Write each group in red or black if it has a fail
                    String color = (talonGroup.hasFail() ? "red" : "black");
                    subsystemContent += HTML.header(talonGroup.getName(), 2, color);

                    // Write config as individual lines
                    String[] config = talonGroup.getConfig().configAsString().split("\n");
                    for (String configLine : config)
                        subsystemContent += HTML.paragraph(configLine);

                    String table = "";

                    // Write Average to table
                    table += HTML.tableRow(HTML.tableCell("Average")
                            + HTML.tableCell(talonGroup.getAverageForwardAmperage().toString())
                            + HTML.tableCell(talonGroup.getAverageReverseAmperage().toString()));

                    // Headers
                    table += HTML.tableRow(HTML.tableHeader("Talon") + HTML.tableHeader("Forward Amperage")
                            + HTML.tableHeader("Reverse Amperage"));

                    // Loop through every talon
                    for (int talon = 0; talon < talonSize + 1; talon++) {
                        String row = "";

                        boolean fwdFail = fwd.get(talon).getFail();
                        boolean revFail = rev.get(talon).getFail();

                        // Put talon cell
                        String talonCell;
                        if (fwdFail || revFail)
                            talonCell = HTML.tableCell(mtr.get(talon).getGZName(), "yellow", false);
                        else
                            talonCell = HTML.tableCell(mtr.get(talon).getGZName());

                        row += talonCell;

                        // Populate forward cell
                        String fwdCell;
                        if (fwdFail)
                            fwdCell = HTML.tableCell(fwd.get(talon).getCurrent().toString(), "red", false);
                        else
                            fwdCell = HTML.tableCell(fwd.get(talon).getCurrent().toString());
                        row += fwdCell;

                        // Populate reverse cell
                        String revCell;
                        if (revFail)
                            revCell = HTML.tableCell(rev.get(talon).getCurrent().toString(), "red", false);
                        else
                            revCell = HTML.tableCell(rev.get(talon).getCurrent().toString());
                        row += revCell;

                        // Format as row
                        row = HTML.tableRow(row);

                        // add to table
                        table += row;
                    } // end of Loop through every talon

                    // group has all talons written to table
                    // format as table and add to subsystem
                    table = HTML.table(table);
                    subsystemContent += table;

                } // end of all groups in subsystem loop

                // if the subsystem doesn't have any fails, wrap in a button so we can hide it
                // easily
                if (!subsystem.hasMotorTestingFail())
                    subsystemContent = HTML.button("Open " + subsystem.toString(), subsystemContent);

                body += subsystemContent;
            } // end of all subsystems

            boolean htmlWriteFail = false;
            GZFile file = null;
            try {
                // write to rio
                file = GZFileMaker.getFile("MotorReport-" + GZUtil.dateTime(false), new Folder("MotorReports"),
                        ValidFileExtension.HTML, false, true);
                HTML.createHTMLFile(file, body);

            } catch (Exception e) {
                System.out.println("Could not write MotorReport!");
                htmlWriteFail = true;
            }

            try {
                GZFiles.copyFile(file, GZFileMaker.getFile(file, true));
            } catch (Exception copyFile) {
                System.out.println("Could not copy MotorReport to USB!");
                htmlWriteFail = true;
            }

            if (!htmlWriteFail)
                System.out.println("MotorReport successfully written!");

            clearValues();

            Robot.allSubsystems.enableFollower();
        }

    }

    public static class PDPChannelChecker {

        public static class CheckerConfig {
            private final double mRunTimeSec;
            private final double mRunSpeed;
            private final GZSubsystem mSubsystem;

            public CheckerConfig(GZSubsystem subsystem, double runtimeSec, double runSpeed) {
                this.mSubsystem = subsystem;
                this.mRunSpeed = runSpeed;

                if (runtimeSec < .1)
                    this.mRunTimeSec = .1;
                else
                    this.mRunTimeSec = runtimeSec;
            }

            public GZSubsystem getSubsystem() {
                return this.mSubsystem;
            }

            public double getRunTime() {
                return this.mRunTimeSec;
            }

            public double getRunSpeed() {
                return this.mRunSpeed;
            }
        }

        public static class CheckerMotorGroup {
            private final List<GZSpeedController> mController;
            private final CheckerConfig mConfig;
            private final String mName;

            public CheckerMotorGroup(String name, List<GZSpeedController> c, CheckerConfig config) {
                this.mController = c;
                this.mConfig = config;
                this.mName = name;
            }

            public String getName() {
                return this.mName;
            }

            public List<GZSpeedController> getControllers() {
                return this.mController;
            }

            public CheckerConfig getConfig() {
                return this.mConfig;
            }
        }

        private ArrayList<CheckerMotorGroup> groupsToTest;

        private static PDPChannelChecker mInstance = null;

        private GZPDP pdp = GZPDP.getInstance();

        public static PDPChannelChecker getInstance() {
            if (mInstance == null)
                mInstance = new PDPChannelChecker();
            return mInstance;
        }

        private PDPChannelChecker() {
            clearValues();
        }

        public void clearValues() {
            groupsToTest = new ArrayList<CheckerMotorGroup>();
        }

        public void addGroup(CheckerMotorGroup group) {
            groupsToTest.add(group);
        }

        public List<GZSubsystem> getSubsystemsBeingTested() {
            List<GZSubsystem> ret = new ArrayList<GZSubsystem>();
            for (CheckerMotorGroup c : groupsToTest) {
                if (!ret.contains(c.getConfig().getSubsystem()))
                    ret.add(c.getConfig().getSubsystem());
            }

            return ret;
        }

        public void runCheck(double waitTime) {
            String body = HTML.paragraph("File created on: " + GZUtil.dateTime(false));

            System.out.println("RUNNING PDP CHANNEL CHECK");
            {
                String print = "Subsystems being checked: ";
                for (GZSubsystem s : getSubsystemsBeingTested())
                    print += s.toString() + ",";

                System.out.println(print);
            }

            for (CheckerMotorGroup c : groupsToTest) {
                String group = HTML.header(c.getConfig().getSubsystem().toString(), 1);

                for (GZSpeedController speed_Controller : c.getControllers()) {
                    CheckControllerReturn result = checkController(speed_Controller, c.getConfig());

                    String controller = "";

                    controller += HTML.header(speed_Controller.getGZName());

                    // Potential channels
                    {
                        String potentialChannels = "Potential Channels: ";
                        for (int i = 0; i <= 15; i++)
                            if (result.pdpVals.get(i).trip)
                                potentialChannels += "[" + i + "]" + "\t";

                        controller += HTML.paragraph(potentialChannels, "red");
                    }

                    String hidden = "";
                    // AVERAGE
                    {
                        String average = "Average current: " + GZUtil.roundTo(result.average, 2);
                        hidden += HTML.paragraph(average);
                    }

                    // TABLE
                    String table = "";
                    {
                        String nextRow = "";
                        nextRow += HTML.tableHeader("PDP Channel");
                        for (int i = 0; i <= 15; i++)
                            nextRow += HTML.tableCell(String.valueOf(i));

                        nextRow = HTML.tableRow(nextRow);
                        table += nextRow;
                    }
                    {
                        String nextRow = "";
                        nextRow += HTML.tableHeader("Current");
                        for (int i = 0; i <= 15; i++) {
                            nextRow += HTML.tableCell(GZUtil.roundTo(result.pdpVals.get(i).val, 2).toString(),
                                    result.pdpVals.get(i).trip ? "red" : "white", false);
                        }
                        nextRow = HTML.tableRow(nextRow);

                        table += nextRow;
                    }

                    table = HTML.table(table);
                    hidden += table;
                    hidden = HTML.button("Values", hidden);

                    controller += hidden;

                    group += controller;

                    Timer.delay(waitTime);
                } // end of all controllers in group

                body += group;
            } // end of all groups

            boolean fail = false;
            try {
                GZFile file = GZFileMaker.getFile("PDPCheck", new Folder(""), ValidFileExtension.HTML, false, true);
                HTML.createHTMLFile(file, body);
                try {
                    GZFiles.copyFile(file, GZFileMaker.getFile(file, true));
                } catch (Exception e) {
                    System.out.println("Could not copy PDP check to USB!");
                }
            } catch (Exception e) {
                System.out.println("Could not write PDP check!");
                fail = true;
            }
            System.out.println(fail ? "WARNING " : "" + "PDP check written " + (fail ? "un" : "") + "successfully");

            clearValues();
        }

        public static class AmperageValue {
            final Double val;
            boolean trip = false;

            public AmperageValue(double val) {
                this.val = val;
            }

            public void trip() {
                trip = true;
            }
        }

        private static class CheckControllerReturn {
            ArrayList<AmperageValue> pdpVals;
            double average;

            public CheckControllerReturn(ArrayList<AmperageValue> pdpVals, double average) {
                this.pdpVals = pdpVals;
                this.average = average;
            }
        }

        private CheckControllerReturn checkController(GZSpeedController c, final PDPChannelChecker.CheckerConfig config) {
            ArrayList<AmperageValue> pdpVals = new ArrayList<AmperageValue>();
            double currentAverage = 0;
            try {
                System.out.println("Checking " + config.getSubsystem().toString() + " - " + c.getGZName());

                c.lockOutController(true);

                c.set(config.getRunSpeed(), true);
                Timer.delay(config.getRunTime());

                List<Integer> channelsToFake = null;
                if (pdp.isFake())
                    channelsToFake = Arrays.asList(GZUtil.getRandInt(0, 15), GZUtil.getRandInt(0, 15));

                for (int i = 0; i <= 15; i++) {

                    // In case we're simulating, we can still test this function
                    double fakeCurrent = 0;
                    if (channelsToFake != null)
                        if (channelsToFake.contains(i))
                            fakeCurrent = GZUtil.getRandDouble(0, 4);

                    double current = pdp.getCurrent(i, fakeCurrent);
                    pdpVals.add(new AmperageValue(current));
                    currentAverage += current;
                }

                c.set(0, true);

                currentAverage /= 16;

                for (int i = 0; i <= 15; i++) {
                    if (pdpVals.get(i).val > currentAverage)
                        pdpVals.get(i).trip();
                }

            } catch (Exception e) {

            } finally {
                c.lockOutController(false);
            }

            return new CheckControllerReturn(pdpVals, currentAverage);
        }

    }
}