package frc.robot.util;

import java.io.File;

import frc.robot.util.GZFileMaker.ValidFileExtension;

public class GZFile {
    private final String mName;
    private final GZFiles.Folder mFolder;
    private final boolean mUsb, mWrite;
    private final ValidFileExtension mFileExtension;
    private final File mFile;

    public GZFile(String name, GZFiles.Folder folder, ValidFileExtension extension, boolean usb, boolean write, File f) {
        this.mName = name;
        this.mFolder = folder;
        this.mFileExtension = extension;
        this.mUsb = usb;
        this.mWrite = write;
        this.mFile = f;
    }

    public String getName() {
        return this.mName;
    }

    public GZFiles.Folder getFolder() {
        return this.mFolder;
    }

    public ValidFileExtension getFileExtension() {
        return this.mFileExtension;
    }
    

    public boolean isOnUsb() {
        return this.mUsb;
    }

    public boolean isWrite()
    {
        return this.mWrite;
    }

    public File getFile() {
        return this.mFile;
    }

}