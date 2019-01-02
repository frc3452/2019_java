package frc.robot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import frc.robot.Constants.kFiles;
import frc.robot.util.GZFile;
import frc.robot.util.GZFileMaker;
import frc.robot.util.GZFileMaker.ValidFileExtension;
import frc.robot.util.GZFiles;
import frc.robot.util.GZFiles.Folder;
import frc.robot.util.GZFlag;
import frc.robot.util.GZFlagMultiple;
import frc.robot.util.GZNotifier;
import frc.robot.util.GZTimer;
import frc.robot.util.GZUtil;
import frc.robot.util.LatchedBoolean;
import frc.robot.util.PersistentInfo;

public class PersistentInfoManager {

    // Map linking Name of setting to value
    private Map<String, PersistentInfo> mSettingsMap = new HashMap<String, PersistentInfo>();

    // timers and prev vals
    private final GZTimer mOnTimeTimer = new GZTimer("OnTime");
    private final GZTimer mEnabledTimer = new GZTimer("EnabledTimer");

    private GZNotifier mUpdateNotifier;
    private GZFlag mReadFailed = new GZFlag();
    private GZFlagMultiple mResetFlag;

    private LatchedBoolean mResetFlagLatchedBoolean = new LatchedBoolean();

    private PersistentInfo mEnabledTime = new PersistentInfo() {
        public void update() {
            this.addDifference(mEnabledTimer.getTotalTimeRunning());
        }

        public void readSetting() {
        }
    };

    private PersistentInfo mOnTime = new PersistentInfo() {
        public void update() {
            this.addDifference(mOnTimeTimer.get());
        }

        public void readSetting() {
        }
    };

    private PersistentInfo mLeftEncoderRotations = new PersistentInfo(0.0, .01) {
        public void update() {
            // this.addDifference(drive.mIO.left_encoder_total_delta_rotations);
        }

        public void readSetting() {
        }
    };
    private PersistentInfo mRightEncoderRotations = new PersistentInfo(0.0, .01) {
        public void update() {
            // this.addDifference(drive.mIO.right_encoder_total_delta_rotations);
        }

        public void readSetting() {
        }
    };

    private PersistentInfo mDisabled = new PersistentInfo() {
        public void update() {
            this.setValue(GZOI.getInstance().isSafetyDisabled() ? 1.0 : 0.0);
        }

        public void readSetting() {
            GZOI.getInstance().setSafetyDisable(this.getValue() == 1.0);
        }
    };

    private void resetMap() {
        mSettingsMap = new HashMap<String, PersistentInfo>();

        mSettingsMap.put("EnabledTime", mEnabledTime);
        mSettingsMap.put("OnTime", mOnTime);
        mSettingsMap.put("LeftEncoderRot", mLeftEncoderRotations);
        mSettingsMap.put("RightEncoderRot", mRightEncoderRotations);
        mSettingsMap.put("Disabled", mDisabled);
    }

    private static PersistentInfoManager mInstance = null;

    public static PersistentInfoManager getInstance() {
        if (mInstance == null)
            mInstance = new PersistentInfoManager();

        return mInstance;
    }

    // On startup put values in map and start timer
    private PersistentInfoManager() {
        resetMap();
        mOnTimeTimer.startTimer();
    }

    // Read from file and folder on RIO
    public void readOnStartup(String fileName, Folder folder) {
        readOnStartup(fileName, folder, false);
    }

    private boolean backupFile() {
        boolean fail = false;

        try {
            GZFiles.copyFile(
                    GZFileMaker
                            .getFile(kFiles.STATS_FILE_NAME, kFiles.STATS_FILE_FOLDER, kFiles.STATS_FILE_ON_USB, false)
                            .getFile(),
                    GZFileMaker.getFile("StatsBackup-" + GZUtil.dateTime(true), kFiles.STATS_FILE_FOLDER, true, true)
                            .getFile());
        } catch (Exception e) {
            // e.printStackTrace();
            fail = true;
            // e.printStackTrace();
        }

        System.out.println(
                (fail ? "WARNING " : "") + "Stats file backed up " + (fail ? "unsuccessfully" : "successfully"));
        return !fail;
    }

    public void initialize() {
        initialize(false);
    }

    public void initialize(boolean createNewFile) {
        if (!createNewFile) {
            backupFile();
            readOnStartup(kFiles.STATS_FILE_NAME, kFiles.STATS_FILE_FOLDER, kFiles.STATS_FILE_ON_USB);
        }
        updateFile(kFiles.STATS_FILE_NAME, kFiles.STATS_FILE_FOLDER, kFiles.STATS_FILE_ON_USB);
    }

    /**
     * Read from file and folder on RIO or USB Store values into map Call read
     * setting, which (if defined) could update a robot variable with setting
     */
    public void readOnStartup(String fileName, Folder folder, boolean usb) {
        mReadFailed = new GZFlag();
        resetMap();

        try {
            // Set up fille reading
            File f = GZFileMaker.getFile(fileName, folder, usb, false).getFile();
            Scanner scnr = new Scanner(new FileReader(f));

            // loop through lines but drop out if it fails
            while (scnr.hasNext() && !mReadFailed.isFlagTripped()) {
                String t = scnr.nextLine();
                String split[] = t.split(",");

                // if map contains that setting
                if (mSettingsMap.containsKey(split[0])) {
                    // Get setting and read in value
                    mSettingsMap.get(split[0]).setValue(Double.valueOf(split[1]));
                } else {
                    // Map doesn't have setting
                    System.out.println("ERROR Could not read setting " + split[0] + ".");

                    // For some reason a value is in the file and not in the map, so something isn't
                    // right in the first place
                    // Trip the flag incase so we don't accidentally overwrite any data
                    mReadFailed.tripFlag();
                }
            }

            // Close scanner
            scnr.close();

            // If any PersistentInfos do anything on startup like refresh variables in other
            // files, have
            // them do it now
            if (!mReadFailed.isFlagTripped())
                readSettings();

        } catch (Exception e) {
            // Couldn't read persistent settings
            mReadFailed.tripFlag();
            System.out.println("WARNING ERROR Could not read persistent settings at file location "
                    + GZFileMaker.getFileLocation(fileName, folder, ValidFileExtension.CSV, usb, true));
        }

        if (!mReadFailed.isFlagTripped())
            System.out.println("Persistent settings read correctly.");
    }

    private void readSettings() {
        for (PersistentInfo p : mSettingsMap.values())
            p.readSetting();
    }

    private void updateValues() {
        for (PersistentInfo p : mSettingsMap.values())
            p.update();
    }

    public void printPersistentSettings() {
        System.out.println("~~~Persistent settings" + (mReadFailed.isFlagTripped() ? " on temp file" : "") + "~~~");
        for (String s : mSettingsMap.keySet())
            System.out.println(s + "\t\t\t" + mSettingsMap.get(s).getValue());
    }

    public void replaceAndReRead() {
        try {
            GZFile theOld = GZFileMaker.getFile(kFiles.STATS_FILE_NAME, kFiles.STATS_FILE_FOLDER,
                    ValidFileExtension.CSV, kFiles.STATS_FILE_ON_USB, false);
            GZFile theNew = GZFileMaker.getFile("StatsToReplace", kFiles.STATS_FILE_FOLDER, ValidFileExtension.CSV,
                    false, false);
            GZFiles.replaceFile(theOld, theNew);

            initialize();
        } catch (Exception e) {
        }
    }

    // Update file every __ seconds @ file and folder on RIO
    public void updateFile(final String fileName, final Folder folder) {
        updateFile(fileName, folder, false);
    }

    public void reset() {
        // If backup worked
        if (backupFile()) {
            // reset values
            for (PersistentInfo p : mSettingsMap.values())
                p.setValueToDefault();

            mOnTimeTimer.startTimer();
            mEnabledTimer.stop();
            mEnabledTimer.clearTotalTimeRunning();

            System.out.println("GZStats reset");
        } else {
            System.out.println("WARNING GZStats could not be reset because the backup failed");
        }
    }

    public void updateFile(String fileName, Folder folder, final boolean usb) {

        final String mFileName;
        final Folder mFolder;
        final boolean mUsb;

        // If flag we failed while reading, write to a temporary file so that we don't
        // accidentally overrwrite the old one
        if (mReadFailed.isFlagTripped()) {
            mFileName = GZUtil.dateTime(false);
            mFolder = Constants.kFiles.STATS_FILE_FOLDER;
            mUsb = Constants.kFiles.STATS_FILE_ON_USB;
            System.out.println("ERROR Could not read startup file! Writing new file to " + mFolder.get(mUsb) + "/"
                    + mFileName + ".csv" + " on RIO");

        } else {
            mFileName = fileName;
            mFolder = folder;
            mUsb = usb;
        }

        // Define notifier and runnable
        mUpdateNotifier = new GZNotifier(new Runnable() {
            public void run() {

                // get new values
                updateValues();

                try {
                    // SETUP FILE WRITING

                    File f = GZFileMaker.getFile(mFileName, mFolder, mUsb, true).getFile();

                    // create file writing vars
                    BufferedWriter bw = new BufferedWriter(new FileWriter(f));

                    // write values
                    // LeftEncoderTicks,1024
                    // Disabled,0
                    for (String v : mSettingsMap.keySet()) {
                        bw.write(v + "," + mSettingsMap.get(v).getValue());
                        bw.write("\r\n");
                    }

                    bw.close();

                } catch (Exception e) {
                    // e.printStackTrace();
                    System.out.println("ERROR Could not update long term stats file!");
                }
            }
        });

        // only start once
        if (!mUpdateNotifier.hasStarted())
            mUpdateNotifier.startPeriodic(kFiles.DEFAULT_STATS_RECORD_TIME);
    }

    public void robotEnabled() {
        mEnabledTimer.startTimer();
    }

    public void robotDisabled() {
        mEnabledTimer.stopTimer();
    }

}