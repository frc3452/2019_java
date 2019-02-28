package frc.robot.auto.commands.functions.drive.pathfollowing;

public class FlippedPath {
    private final PathContainer mLeftPath;
    private final PathContainer mRightPath = null;

    public FlippedPath(PathContainer pc)
    {
        this.mLeftPath = pc;
        
    }

    public PathContainer get(boolean left)
    {
        if (left)
            return getLeft();

        return getRight();
    }

    public PathContainer getLeft()
    {
        return null;
    }    

    public PathContainer getRight()
    {
        return null;
    }



}