package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants;
import frc.robot.Constants.kAuton;
import frc.robot.GZOI;
import frc.robot.auto.commands.AutoModeBuilder.EncoderMovement;
import frc.robot.poofs.util.math.Pose2d;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.poofs.util.math.Translation2d;
import frc.robot.subsystems.Drive.RocketIdentifcation;
import frc.robot.util.GZFiles;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZSubsystemManager;
import frc.robot.util.requests.QuickCompleteRequest;
import frc.robot.util.requests.Request;
import frc.robot.util.requests.RequestList;
import frc.robot.util.requests.RequestManager;

import java.util.ArrayList;
import java.util.Arrays;

public class Superstructure extends GZSubsystem {


    private GZSubsystemManager subsystems;

    private RequestManager manager = new RequestManager();
    private static Superstructure mInstance = null;

  
    public static Superstructure getInstance() {
        if (mInstance == null)
            mInstance = new Superstructure();
        return mInstance;
    }

    @Override
    public void loop() {
        if (GZOI.getInstance().isEnabled()) {
            for (int i = 0; i < 10; i++) {
                manager.update(Timer.getFPGATimestamp());
            }
        }
    }



    public void cancel() {
        manager.clear();
        manager.request(new RequestList(this).extraLog("Superstructure cleared"));
    }
        public boolean _isFinished() {
            return true;
    }
        
    


    public String getStateString() {
        return "";
    }

    @Override
    public void stop() {
        subsystems.stop();
    }

    @Override
    public String getSmallString() {
        return "SPR-STRCT";
    }

    public void addLoggingValues() {
    }

    protected void initDefaultCommand() {
    }

    

}