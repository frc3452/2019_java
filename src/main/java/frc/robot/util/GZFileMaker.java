package frc.robot.util;

import java.io.File;
import java.io.IOException;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.util.GZFiles.Folder;

public class GZFileMaker {

    private static String[] illegalCharacters = { "*", "?", "<", ">", "|" };

    private static String placeForInvalidFilesToGo = "InvalidFileName";

    public static enum ValidFileExtension {
        CSV(".csv"), HTML(".html");

        private String val;

        private ValidFileExtension(String val) {
            this.val = val;
        }
    }

    public static GZFile getFile(GZFile file, boolean usb) throws Exception {
        return getFile(file, usb, file.isWrite());
    }

    public static GZFile getFile(GZFile file, boolean usb, boolean write) throws Exception {
        GZFile ret;
        ret = getFile(file.getName(), file.getFolder(), file.getFileExtension(), write, usb);
        return ret;
    }

    public static GZFile getSafeFile(String name, Folder folder, ValidFileExtension fileExtension, boolean usb,
            boolean write) {
        try {
            return getFile(name, folder, fileExtension, usb, write);
        } catch (Exception e) {
            return null;
        }
    }

    public static GZFile getFile(String name, Folder folder, ValidFileExtension fileExtension, boolean usb,
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

        GZFile ret = new GZFile(name, folder, fileExtension, usb, write, f);
        return ret;
    }

    public static GZFile getFile(String name, Folder folder, ValidFileExtension fileExtension, boolean write)
            throws Exception {
        return getFile(name, folder, fileExtension, false, write);
    }

    public static GZFile getFile(String name, Folder folder, boolean usb, boolean write) throws Exception {
        return getFile(name, folder, ValidFileExtension.CSV, usb, write);
    }

    public static GZFile getFile(String name, Folder folder, boolean write) throws Exception {
        return getFile(name, folder, false, write);
    }

    public static String getFileLocation(String name, Folder folder, ValidFileExtension fileExtension, boolean usb,
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
            retval = ((usb ? "/u/" : "/home/lvuser/") + realFolderText + (folderText.equals("") ? "" : "/"));
        }

        if (withFile)
            retval += (folderText.equals("") ? "" : (sim ? "\\" : "/")) + name + fileExtension.val;

        if (pathValid(retval))
            return retval;
        else {
            String newRetval = (sim ? Filesystem.getLaunchDirectory().getAbsolutePath() + "\\" : "/home/lvuser/")
                    + placeForInvalidFilesToGo + "/" + GZUtil.dateTime(true) + fileExtension.val;

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