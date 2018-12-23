package frc.robot.util;

import java.io.File;

import frc.robot.util.GZFileMaker.ValidFileExtensions;

public class GZFile {
    private String mName;
    private GZFiles.Folder mFolder;
    private boolean mUsb;
    private ValidFileExtensions mFileExtension;
    private File mFile;

    public GZFile(String name, GZFiles.Folder folder, ValidFileExtensions extension, boolean usb, File f) {
        this.mName = name;
        this.mFolder = folder;
        this.mFileExtension = extension;
        this.mUsb = usb;
        this.mFile = f;
    }

    public String getName() {
        return this.mName;
    }

    public GZFiles.Folder getFolder() {
        return this.mFolder;
    }

    public ValidFileExtensions getFileExtension() {
        return this.mFileExtension;
    }

    public boolean isOnUsb() {
        return this.mUsb;
    }

    public File getFile() {
        return this.mFile;
    }

}