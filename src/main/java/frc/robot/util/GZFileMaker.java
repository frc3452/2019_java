package frc.robot.util;

import java.io.File;
import java.io.IOException;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.util.GZFiles.Folder;

public class GZFileMaker {

    private static String[] illegalCharacters = { "*", "?", "<", ">", "|" };

    private static String placeForInvalidFilesToGo = "InvalidFileName";

    public static enum ValidFileExtensions {
        CSV(".csv"), HTML(".html");

        private String val;

        private ValidFileExtensions(String val) {
            this.val = val;
        }
    }

    public static GZFile getFile(GZFile g, boolean usb) throws Exception {
        GZFile f;
        f = getFile(g.getName(), g.getFolder(), g.getFileExtension(), usb);
        return f;
    }

    public static GZFile getFile(String name, Folder folder, ValidFileExtensions fileExtension, boolean usb,
            boolean write) throws Exception {

        String path = getFileLocation(name, folder, fileExtension, usb, true);
        File f = new File(path);


        
        if (write) {

            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();

            try {
                if (!f.exists())
                    f.createNewFile();
            } catch (Exception e) {
                // e.printStackTrace();
            }

        } else {
        }

        if (!f.exists() && !write) {
            throw new IOException("ERROR File cannot be found at path {" + path + "}");
        }

        GZFile ret = new GZFile(name, folder, fileExtension, usb, f);
        return ret;
    }

    public static GZFile getFile(String name, Folder folder, ValidFileExtensions fileExtension, boolean write)
            throws Exception {
        return getFile(name, folder, fileExtension, false, write);
    }

    public static GZFile getFile(String name, Folder folder, boolean usb, boolean write) throws Exception {
        return getFile(name, folder, ValidFileExtensions.CSV, usb, write);
    }

    public static GZFile getFile(String name, Folder folder, boolean write) throws Exception {
        return getFile(name, folder, false, write);
    }

    public static String getFileLocation(String name, Folder folder, ValidFileExtensions fileExtension, boolean usb,
            boolean withFile) {
        String folderText = folder.get(usb);
        String retval;

        boolean sim = RobotBase.isSimulation();

        String realFolderText = folderText;
        if (sim && !usb)
            realFolderText = "\\RIO\\" + folderText;

        if (sim) {
            retval = Filesystem.getLaunchDirectory().getAbsolutePath() + "\\" + realFolderText;
        } else {
            retval = ((usb ? "/u/" : "/home/lvuser/") + realFolderText + (folderText == "" ? "" : "\\"));
        }

        if (withFile)
            retval += (folderText == "" ? "" : "\\") + name + fileExtension.val;

        if (pathValid(retval))
            return retval;
        else {
            String newRetval = (sim ? Filesystem.getLaunchDirectory().getAbsolutePath() + "\\" : "/home/lvuser/")
                    + placeForInvalidFilesToGo + "\\" + GZUtil.dateTime(true) + fileExtension.val;

            System.out.println("Invalid file location: " + retval + "\nFile will be written at: " + newRetval);
            return newRetval;
        }
    }

    public static boolean pathValid(String path) {
        boolean containsBadValue = false;

        for (String s : illegalCharacters)
            containsBadValue |= stringContainsChar(path, s);

        // Method should returning if valid, not if contains bad characters
        return !containsBadValue;
    }

    private static boolean stringContainsChar(String name, String character) {
        return name.contains(character);
    }

}