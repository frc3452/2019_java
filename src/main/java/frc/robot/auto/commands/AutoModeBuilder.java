package frc.robot.auto.commands;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.auto.commands.AutoModeBuilder.ScoringPosition.ScoringPosLimitations;
import frc.robot.auto.commands.AutoModeBuilder.ScoringPosition.ScoringPosLimitations.AutoDirection;
import frc.robot.auto.commands.functions.NoCommand;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.superstructure.ExtendSlides;
import frc.robot.auto.commands.functions.superstructure.GoToHeight;
import frc.robot.auto.commands.functions.superstructure.RunAction;
import frc.robot.auto.commands.paths.center.Center_CS_Bay_1_Left;
import frc.robot.auto.commands.paths.center.Center_CS_Bay_2_Left;
import frc.robot.auto.commands.paths.center.Center_CS_Bay_3_Left;
import frc.robot.auto.commands.paths.center.Center_CS_Face_Left;
import frc.robot.auto.commands.paths.center.Center_Rocket_Close_Left;
import frc.robot.auto.commands.paths.center.Center_Rocket_Far_Left;
import frc.robot.auto.commands.paths.center.Center_Rocket_Mid_Left;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_CS_Face_1;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_CS_Face_Opp_2;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_CS_Face_Same_2;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_CS_Side_Bay_1;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_CS_Side_Bay_2;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_CS_Side_Bay_3;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_CS_Side_Opp_1;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_CS_Side_Same_1;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_Rocket_Close_1_Opp;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_Rocket_Close_1_Same;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_Rocket_Close_2_Opp;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_Rocket_Close_2_Same;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_Rocket_Far_1_Opp;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_Rocket_Far_1_Same;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_Rocket_Far_2_Opp;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_Rocket_Far_2_Same;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_Rocket_Mid_1_Opp;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_Rocket_Mid_1_Same;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_Rocket_Mid_2_Opp;
import frc.robot.auto.commands.paths.feeder_station_to.Feeder_Station_To_Rocket_Mid_2_Same;
import frc.robot.auto.commands.paths.left.Left_CS_Bay_1_Opp;
import frc.robot.auto.commands.paths.left.Left_CS_Bay_1_Same;
import frc.robot.auto.commands.paths.left.Left_CS_Bay_2_Opp;
import frc.robot.auto.commands.paths.left.Left_CS_Bay_2_Same;
import frc.robot.auto.commands.paths.left.Left_CS_Bay_3_Opp;
import frc.robot.auto.commands.paths.left.Left_CS_Bay_3_Same;
import frc.robot.auto.commands.paths.left.Left_CS_Face_Opp;
import frc.robot.auto.commands.paths.left.Left_CS_Face_Same;
import frc.robot.auto.commands.paths.left.Left_Rocket_Close_Opp;
import frc.robot.auto.commands.paths.left.Left_Rocket_Close_Same;
import frc.robot.auto.commands.paths.left.Left_Rocket_Far_Opp;
import frc.robot.auto.commands.paths.left.Left_Rocket_Far_Same;
import frc.robot.auto.commands.paths.left.Left_Rocket_Far_Same_Backwards;
import frc.robot.auto.commands.paths.left.Left_Rocket_Mid_Opp;
import frc.robot.auto.commands.paths.left.Left_Rocket_Mid_Same;
import frc.robot.auto.commands.paths.to_feeder_station.CS_Face_Turn_Around_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.CS_Face_Turn_Around_Same;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_1_Turn_Around;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_1_Turn_Around_2_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_2_Turn_Around_1;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_2_Turn_Around_2;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_2_Turn_Around_2_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_3_Turn_Around_1;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_3_Turn_Around_2;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_3_Turn_Around_2_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Close_Turn_Around_2_If_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Close_Turn_Around_Same;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Far_Backwards_Turn_Around_1;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Far_Turn_Around_1;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Far_Turn_Around_2_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Far_Turn_Around_2_Same;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Mid_Turn_Around_2_If_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Mid_Turn_Around_Same;
import frc.robot.auto.commands.paths.to_feeder_station.To_Feeder_Station_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.To_Feeder_Station_Same_Shallow;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.subsystems.Auton;
import frc.robot.subsystems.Superstructure;
import frc.robot.subsystems.Superstructure.Actions;
import frc.robot.util.GZCommand;
import frc.robot.util.GZCommandGroup;
import frc.robot.util.GZUtil;

public class AutoModeBuilder {

    public static final ArrayList<StartingPosition> AllStartingPositions = new ArrayList<StartingPosition>();
    public static final ArrayList<ScoringPosition> AllScoringPositions = new ArrayList<ScoringPosition>();
    public static final ArrayList<FeederStation> AllFeederStations = new ArrayList<FeederStation>();
    public static final ArrayList<ScoringSide> AllScoringSides = new ArrayList<ScoringSide>();

    private static FeederStation mFeederStation = null;

    public static void setFeederStation(FeederStation station) {
        mFeederStation = station;
    }

    public static boolean hasSelectedFeederStation() {
        return mFeederStation != null;
    }

    static {
        if (StartingPosition.LEFT == null) {
        }

        if (ScoringPosition.CARGO_SHIP_BAY_1 == null) {
        }

        if (FeederStation.LEFT == null) {
        }

        if (ScoringSide.LEFT == null) {
        }
    }

    public static enum StartingPosition {
        CENTER(false, false, "Center"), LEFT(true, false, "Left"), RIGHT(false, true, "Right");

        private final boolean onLeft;
        private final boolean onRight;
        private final String name;

        private StartingPosition(boolean onLeft, boolean onRight, String name) {
            this.onLeft = onLeft;
            this.onRight = onRight;
            this.name = name;

            AllStartingPositions.add(this);
        }

        public String toString() {
            return name;
        }
    }

    public static class ScoringLocation {
        private final ScoringPosition pos;
        private final ScoringSide side;

        public ScoringLocation(ScoringPosition pos, ScoringSide side) {
            this.pos = pos;
            this.side = side;
        }

        @Override
        public String toString() {
            return this.pos.toString() + " (" + this.side.toString() + ")";
        }

        public boolean isOnCargoShip() {
            return this.pos.cargoShip;
        }
    }

    public enum ScoringPosition {
        CARGO_SHIP_FACE("Cargo Ship Face"), CARGO_SHIP_BAY_1("Cargo Ship Bay 1"), CARGO_SHIP_BAY_2("Cargo Ship Bay 2"),
        CARGO_SHIP_BAY_3("Cargo Ship Bay 3"), ROCKET_NEAR("Rocket Near Face", false),
        ROCKET_MID("Rocket Middle Face", false), ROCKET_FAR("Rocket Far Face", false),
        ROCKET_FAR_REVERSE("Rocket Far Face (Reverse)", false,
                new ScoringPosLimitations().canDoNothing().canBackwardsSameSide().canSameSideFeeder());

        public final String text;
        public final boolean cargoShip;
        public final ScoringPosLimitations limitations;

        private ScoringPosition(String text, boolean isCargoShip, ScoringPosLimitations limitations) {
            this.cargoShip = isCargoShip;
            this.text = text;
            this.limitations = limitations;

            AllScoringPositions.add(this);
        }

        private ScoringPosition(String text, boolean isCargoShip) {
            this(text, isCargoShip, new ScoringPosLimitations());
        }

        private ScoringPosition(String text) {
            this(text, true);
        }

        @Override
        public String toString() {
            return text;
        }

        // This really deserves a builder, let's get him one some day
        // Oh my god this desperately needs a builder please help

        /**
         * By default can score everywhere from anywhere forwards
         */
        public static class ScoringPosLimitations {

            public static enum AutoDirection {
                FORWARDS, BACKWARDS
            }

            private boolean oppositeForwards = true;
            private boolean oppositeBackwards = false;

            private boolean sameSideForwards = true;
            private boolean sameSideBackwards = false;

            private boolean centerForwards = true;
            private boolean centerBackwards = false;

            private boolean sameSideFeederStation = true;
            private boolean oppositeSideFeederStation = true;

            public ScoringPosLimitations cantOppositeFeeder() {
                oppositeSideFeederStation = false;
                return this;
            }

            public ScoringPosLimitations canDoNothing() {
                oppositeForwards = false;
                oppositeBackwards = false;

                sameSideForwards = false;
                sameSideBackwards = false;

                centerForwards = false;
                centerBackwards = false;

                sameSideFeederStation = false;
                oppositeSideFeederStation = false;

                return this;
            }

            public ScoringPosLimitations cantCenter() {
                centerBackwards = false;
                centerForwards = false;
                return this;
            }

            public ScoringPosLimitations cantOpposite() {
                oppositeBackwards = false;
                oppositeForwards = false;
                return this;
            }

            public ScoringPosLimitations cantBackwards() {
                centerBackwards = false;
                oppositeBackwards = false;
                sameSideBackwards = false;
                return this;
            }

            public ScoringPosLimitations canBackwards() {
                centerBackwards = true;
                oppositeBackwards = true;
                sameSideBackwards = true;
                return this;
            }

            public ScoringPosLimitations canSameSideFeeder() {
                sameSideFeederStation = true;
                return this;
            }

            public ScoringPosLimitations canBackwardsSameSide() {
                sameSideBackwards = true;
                return this;
            }

        }

    }
    
    public static class EncoderMovement {
        public final double left, right;
        
        public EncoderMovement(double distance)
        {
            this(distance,distance);
        }
        
        public EncoderMovement(double left, double right) {
            this.left = left;
            this.right = right;
        }
    }
    
    public static class AutoMovement {
        public enum TypeOfMovement {
            Path,Distance_Jog,Gyro_Turn
        }
        
        public PathContainer path = null;
        public EncoderMovement jog = null;
        public Rotation2d rotate = null;
        
        public final TypeOfMovement type; 
        
        public AutoMovement(PathContainer path)
        {
            this.path = path;
            this.type = TypeOfMovement.Path;
        }
        
        public AutoMovement(EncoderMovement jog)
        {
            this.jog = jog;   
            this.type = TypeOfMovement.Distance_Jog;
        }
        
        public AutoMovement(Rotation2d rotate)
        {
            this.rotate = rotate;
            this.type = TypeOfMovement.Gyro_Turn;
        }
        
        public TypeOfMovement getType() {
            return type;
        }
    }

    public enum GamePiece {
        CARGO, HATCH_PANEL
    }

    public enum FeederStation {
        LEFT("Left Station", true), RIGHT("Right Station", false);
        private final boolean onLeft;
        private final boolean onRight;
        private final String text;

        private FeederStation(String text, boolean onLeft) {
            this.onLeft = onLeft;
            this.onRight = !this.onLeft;
            this.text = text;

            AllFeederStations.add(this);
        }

        @Override
        public String toString() {
            return text;
        }

    }

    public enum ScoringSide {
        LEFT(true, "Left"), RIGHT(false, "Right");
        private final boolean onLeft;
        private final boolean onRight;
        private final String text;

        private ScoringSide(boolean onLeft, String text) {
            this.onLeft = onLeft;
            this.onRight = !this.onLeft;
            this.text = text;

            AllScoringSides.add(this);
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public static boolean scoringSameSide(StartingPosition startPos, ScoringLocation location) {
        return scoringSameSide(startPos, location.side);
    }

    public static boolean feederSameSide(ScoringLocation location, FeederStation station) {
        if (location.side == ScoringSide.LEFT && station == FeederStation.LEFT)
            return true;
        else if (location.side == ScoringSide.RIGHT && station == FeederStation.RIGHT)
            return true;

        return false;
    }

    public static boolean scoringSameSide(StartingPosition startPos, ScoringSide side) {
        if (startPos == StartingPosition.LEFT && side == ScoringSide.LEFT)
            return true;

        if (startPos == StartingPosition.RIGHT && side == ScoringSide.RIGHT)
            return true;

        return false;
    }

    public static ArrayList<PathContainer> getFirstPath(StartingPosition startPos, ScoringLocation score) {
        switch (score.pos) {
        case CARGO_SHIP_FACE:
            // Center
            if (startPos == StartingPosition.CENTER) {
                return new Center_CS_Face_Left().get(score.side.onLeft).toList();
            }

            // On left or right
            if (scoringSameSide(startPos, score)) {
                return new Left_CS_Face_Same().get(startPos.onLeft).toList();
            } else {
                return new Left_CS_Face_Opp().get(startPos.onLeft).toList();
            }

        case CARGO_SHIP_BAY_1:
            // Center
            if (startPos == StartingPosition.CENTER) {
                return new Center_CS_Bay_1_Left().get(score.side.onLeft).toList();
            }

            // On left or right
            if (scoringSameSide(startPos, score)) {
                return new Left_CS_Bay_1_Same().get(startPos.onLeft).toList();
            } else {
                return new Left_CS_Bay_1_Opp().get(startPos.onLeft).toList();
            }

        case CARGO_SHIP_BAY_2:
            // Center
            if (startPos == StartingPosition.CENTER) {
                return new Center_CS_Bay_2_Left().get(score.side.onLeft).toList();
            }

            // On left or right
            if (scoringSameSide(startPos, score)) {
                return new Left_CS_Bay_2_Same().get(startPos.onLeft).toList();
            } else {
                return new Left_CS_Bay_2_Opp().get(startPos.onLeft).toList();
            }

        case CARGO_SHIP_BAY_3:
            // Center
            if (startPos == StartingPosition.CENTER) {
                return new Center_CS_Bay_3_Left().get(score.side.onLeft).toList();
            }

            // On left or right
            if (scoringSameSide(startPos, score)) {
                return new Left_CS_Bay_3_Same().get(startPos.onLeft).toList();
            } else {
                return new Left_CS_Bay_3_Opp().get(startPos.onLeft).toList();
            }

        case ROCKET_NEAR:
            // Center
            if (startPos == StartingPosition.CENTER) {
                return new Center_Rocket_Close_Left().get(score.side.onLeft).toList();
            }

            // On left or right
            if (scoringSameSide(startPos, score)) {
                return new Left_Rocket_Close_Same().get(startPos.onLeft).toList();
            } else {
                return new Left_Rocket_Close_Opp().get(startPos.onLeft).toList();
            }

        case ROCKET_MID:
            // Center
            if (startPos == StartingPosition.CENTER) {
                return new Center_Rocket_Mid_Left().get(score.side.onLeft).toList();
            }

            // On left or right
            if (scoringSameSide(startPos, score)) {
                return new Left_Rocket_Mid_Same().get(startPos.onLeft).toList();
            } else {
                return new Left_Rocket_Mid_Opp().get(startPos.onLeft).toList();
            }

        case ROCKET_FAR:
            // Center
            if (startPos == StartingPosition.CENTER) {
                return new Center_Rocket_Far_Left().get(score.side.onLeft).toList();
            }

            // On left or right
            if (scoringSameSide(startPos, score)) {
                return new Left_Rocket_Far_Same().get(startPos.onLeft).toList();
            } else {
                return new Left_Rocket_Far_Opp().get(startPos.onLeft).toList();
            }

        case ROCKET_FAR_REVERSE:
            if (startPos == StartingPosition.CENTER) {
                // This shouldn't happen, limitations should handle this
                GZUtil.bigPrint("ROCKET FAR REVERSE CANNOT START FROM CENTER");
                return null;
            }

            if (scoringSameSide(startPos, score)) {
                return new Left_Rocket_Far_Same_Backwards().get(startPos.onLeft).toList();
            } else {
                GZUtil.bigPrint("ROCKET FAR REVERSE CANNOT GO OPPOSITE SIDE OF FIELD");
                return null;
            }
        default:
            System.out.println("[AUTOMODEBUILDER] GET FIRST PATH CASE [" + score.pos + "] null");
            return null;
        }

    }

    public static Command getScoringCommand(ScoringLocation location, GamePiece gamepiece) {
        GZCommandGroup ret = new GZCommandGroup();

        ret.tele();

        // if not scored do everything below

        GZCommandGroup score = new GZCommandGroup();
        switch (gamepiece) {
        case CARGO:
            if (location.isOnCargoShip()) {
                score.add(new GoToHeight(Heights.Cargo_Ship));
            } else {
                score.add(new GoToHeight(Heights.Cargo_1));
            }
            score.add(new RunAction(Actions.THROW_CARGO));
            break;
        case HATCH_PANEL:
            // ret.add(new GoToHeight(Heights.Cargo_1));
            if (location.isOnCargoShip()) {
                score.add(new ExtendSlides());
                score.add(new GoToHeight(Heights.HP_1));
            } else { // rocket
                score.add(new GoToHeight(Heights.HP_2));
            }
            score.add(new RunAction(Actions.SCORE_HATCH));
            break;
        }
        score.tele();

        ConditionalCommand scoreCommand = new ConditionalCommand(new NoCommand(), score) {
            @Override
            protected boolean condition() {
                return Superstructure.getInstance().hasAutoScored();
            }
        };

        ret.add(scoreCommand);

        return ret;
    }

    public static Command prepForScoring(ScoringLocation location, GamePiece gamepiece) {
        switch (location.pos) {
        case CARGO_SHIP_BAY_1:
            break;
        }
        return null;
    }

    public static ArrayList<PathContainer> getScoredPosToFeederStation(final ScoringLocation location,
            final FeederStation station) {
        switch (location.pos) {
        case CARGO_SHIP_FACE: {
            ArrayList<PathContainer> ret = new ArrayList<>();
            if (feederSameSide(location, station)) {
                ret.add(new CS_Face_Turn_Around_Same().get(location.side.onLeft));
                ret.add(new To_Feeder_Station_Same_Shallow().get(station.onLeft));
            } else {
                ret.add(new CS_Face_Turn_Around_Opp().get(location.side.onLeft));
            }
            return ret;
        }

        case CARGO_SHIP_BAY_1: {
            ArrayList<PathContainer> ret = new ArrayList<>();
            ret.add(new Left_CS_Bay_1_Turn_Around().get(location.side.onLeft));
            if (feederSameSide(location, station)) {
                ret.add(new To_Feeder_Station_Same_Shallow().get(station.onLeft));
            } else {
                ret.add(new Left_CS_Bay_1_Turn_Around_2_Opp().get(station.onLeft));
                ret.add(new To_Feeder_Station_Opp().get(station.onLeft));
            }
            return ret;
        }

        case CARGO_SHIP_BAY_2: {
            ArrayList<PathContainer> ret = new ArrayList<>();
            ret.add(new Left_CS_Bay_2_Turn_Around_1().get(location.side.onLeft));
            if (feederSameSide(location, station)) {
                ret.add(new Left_CS_Bay_2_Turn_Around_2().get(location.side.onLeft));
                ret.add(new To_Feeder_Station_Same_Shallow().get(station.onLeft));
            } else {
                ret.add(new Left_CS_Bay_2_Turn_Around_2_Opp().get(location.side.onLeft));
                ret.add(new To_Feeder_Station_Opp().get(station.onLeft));
            }
            return ret;
        }

        case CARGO_SHIP_BAY_3: {
            ArrayList<PathContainer> ret = new ArrayList<>();
            ret.add(new Left_CS_Bay_3_Turn_Around_1().get(location.side.onLeft));
            if (feederSameSide(location, station)) {
                ret.add(new Left_CS_Bay_3_Turn_Around_2().get(location.side.onLeft));
                ret.add(new To_Feeder_Station_Same_Shallow().get(station.onLeft));
            } else {
                ret.add(new Left_CS_Bay_3_Turn_Around_2_Opp().get(location.side.onLeft));
                ret.add(new To_Feeder_Station_Opp().get(station.onLeft));
            }
            return ret;
        }

        case ROCKET_NEAR: {
            ArrayList<PathContainer> ret = new ArrayList<>();
            if (feederSameSide(location, station)) {
                ret.add(new Rocket_Close_Turn_Around_Same().get(location.side.onLeft));
                ret.add(new To_Feeder_Station_Same_Shallow().get(station.onLeft));
            } else {
                ret.add(new Rocket_Close_Turn_Around_Same().get(location.side.onLeft));
                ret.add(new Rocket_Close_Turn_Around_2_If_Opp().get(station.onLeft));
                ret.add(new To_Feeder_Station_Opp().get(station.onLeft));
            }
            return ret;
        }

        case ROCKET_MID: {
            ArrayList<PathContainer> ret = new ArrayList<>();
            if (feederSameSide(location, station)) {
                ret.add(new Rocket_Mid_Turn_Around_Same().get(location.side.onLeft));
                ret.add(new To_Feeder_Station_Same_Shallow().get(station.onLeft));
            } else {
                ret.add(new Rocket_Mid_Turn_Around_Same().get(location.side.onLeft));
                ret.add(new Rocket_Mid_Turn_Around_2_If_Opp().get(station.onLeft));
                ret.add(new To_Feeder_Station_Opp().get(station.onLeft));
            }
            return ret;
        }

        case ROCKET_FAR: {
            ArrayList<PathContainer> ret = new ArrayList<>();
            if (feederSameSide(location, station)) {
                ret.add(new Rocket_Far_Turn_Around_1().get(location.side.onLeft));
                ret.add(new Rocket_Far_Turn_Around_2_Same().get(location.side.onLeft));
                ret.add(new To_Feeder_Station_Same_Shallow().get(station.onLeft));
            } else {
                ret.add(new Rocket_Far_Turn_Around_1().get(location.side.onLeft));
                ret.add(new Rocket_Far_Turn_Around_2_Opp().get(location.side.onLeft));
                ret.add(new To_Feeder_Station_Opp().get(station.onLeft));
            }
            return ret;
        }
        case ROCKET_FAR_REVERSE: {
            ArrayList<PathContainer> ret = new ArrayList<>();
            if (feederSameSide(location, station)) {
                ret.add(new Rocket_Far_Backwards_Turn_Around_1().get(location.side.onLeft));
                ret.add(new To_Feeder_Station_Same_Shallow().get(location.side.onLeft));
            } else {
                GZUtil.bigPrint("ROCKET FAR REVERSE CANNOT SCORE TO FAR FEEDER STATION");
                return null;
            }
            return ret;
        }
        default:
            System.out.println("[AUTOMODEBUILDER] Get scored position to feeder station null! [" + location.pos + "]");
            return null;
        }
    }

    private static ArrayList<PathContainer> getFeederStationToSecondPlacement(FeederStation station,
            ScoringLocation location) {
        switch (location.pos) {
        case CARGO_SHIP_FACE: {

            ArrayList<PathContainer> ret = new ArrayList<>();
            ret.add(new Feeder_Station_To_CS_Face_1().get(station.onLeft));
            if (feederSameSide(location, station)) {
                ret.add(new Feeder_Station_To_CS_Face_Same_2().get(station.onLeft));
            } else {
                ret.add(new Feeder_Station_To_CS_Face_Opp_2().get(station.onLeft));
            }
            return ret;
        }

        case CARGO_SHIP_BAY_1: {

            ArrayList<PathContainer> ret = new ArrayList<>();
            if (feederSameSide(location, station)) {
                ret.add(new Feeder_Station_To_CS_Side_Same_1().get(station.onLeft));
            } else {
                ret.add(new Feeder_Station_To_CS_Side_Opp_1().get(station.onLeft));
            }
            ret.add(new Feeder_Station_To_CS_Side_Bay_1().get(station.onLeft));
            return ret;
        }

        case CARGO_SHIP_BAY_2: {

            ArrayList<PathContainer> ret = new ArrayList<>();
            if (feederSameSide(location, station)) {
                ret.add(new Feeder_Station_To_CS_Side_Same_1().get(station.onLeft));
            } else {
                ret.add(new Feeder_Station_To_CS_Side_Opp_1().get(station.onLeft));
            }
            ret.add(new Feeder_Station_To_CS_Side_Bay_2().get(station.onLeft));
            return ret;
        }

        case CARGO_SHIP_BAY_3: {

            ArrayList<PathContainer> ret = new ArrayList<>();
            if (feederSameSide(location, station)) {
                ret.add(new Feeder_Station_To_CS_Side_Same_1().get(station.onLeft));
            } else {
                ret.add(new Feeder_Station_To_CS_Side_Opp_1().get(station.onLeft));
            }
            ret.add(new Feeder_Station_To_CS_Side_Bay_3().get(station.onLeft));
            return ret;
        }

        case ROCKET_NEAR: {

            ArrayList<PathContainer> ret = new ArrayList<>();
            if (feederSameSide(location, station)) {
                ret.add(new Feeder_Station_To_Rocket_Close_1_Same().get(station.onLeft));
                ret.add(new Feeder_Station_To_Rocket_Close_2_Same().get(station.onLeft));
            } else {
                ret.add(new Feeder_Station_To_Rocket_Close_1_Opp().get(station.onLeft));
                ret.add(new Feeder_Station_To_Rocket_Close_2_Opp().get(station.onLeft));
            }
            return ret;
        }

        case ROCKET_MID: {

            ArrayList<PathContainer> ret = new ArrayList<>();
            if (feederSameSide(location, station)) {
                ret.add(new Feeder_Station_To_Rocket_Mid_1_Same().get(station.onLeft));
                ret.add(new Feeder_Station_To_Rocket_Mid_2_Same().get(station.onLeft));
            } else {
                ret.add(new Feeder_Station_To_Rocket_Mid_1_Opp().get(station.onLeft));
                ret.add(new Feeder_Station_To_Rocket_Mid_2_Opp().get(station.onLeft));
            }
            return ret;
        }

        case ROCKET_FAR: {

            ArrayList<PathContainer> ret = new ArrayList<>();
            if (feederSameSide(location, station)) {
                ret.add(new Feeder_Station_To_Rocket_Far_1_Same().get(station.onLeft));
                ret.add(new Feeder_Station_To_Rocket_Far_2_Same().get(station.onLeft));
            } else {
                ret.add(new Feeder_Station_To_Rocket_Far_1_Opp().get(station.onLeft));
                ret.add(new Feeder_Station_To_Rocket_Far_2_Opp().get(station.onLeft));
            }
            return ret;
        }
        }
        return null;
    }

    public static Command prepForFeederStation() {
        GZCommandGroup ret = new GZCommandGroup();

        // ret.add(new GoToHeight(Heights.HP_1));
        ret = null;

        return ret;
    }

    public static Command retrieveFromFeederStation() {
        GZCommandGroup ret = new GZCommandGroup();
        ret.tele();
        ret.add(new RunAction(Actions.GRAB_HP_FROM_FEED));
        return ret;
    }

    public static GZCommand getCommand(final StartingPosition startPos, final ScoringLocation location,
            final FeederStation nextStation) {
        return getCommand(startPos, AutoDirection.FORWARDS, location, nextStation);
    }

    public static ArrayList<GZCommand> getCommands(final StartingPosition startPos, final ScoringLocation location,
            final FeederStation nextStation) {
        ArrayList<GZCommand> commands = new ArrayList<GZCommand>();

        final ScoringPosLimitations lim = location.pos.limitations;

        final boolean sameSideFeeder = feederSameSide(location, nextStation);

        if ((sameSideFeeder && lim.sameSideFeederStation) || (!sameSideFeeder && lim.oppositeSideFeederStation)) {
            if (startPos == StartingPosition.CENTER) {
                if (lim.centerForwards) {
                    commands.add(getCommand(startPos, AutoDirection.FORWARDS, location, nextStation));
                }
                if (lim.centerBackwards) {
                    commands.add(getCommand(startPos, AutoDirection.BACKWARDS, location, nextStation));
                }
            } else {
                if (scoringSameSide(startPos, location)) {
                    if (lim.sameSideForwards) {
                        commands.add(getCommand(startPos, AutoDirection.FORWARDS, location, nextStation));
                    }
                    if (lim.sameSideBackwards) {
                        commands.add(getCommand(startPos, AutoDirection.BACKWARDS, location, nextStation));
                    }
                } else {
                    if (lim.oppositeForwards) {
                        commands.add(getCommand(startPos, AutoDirection.FORWARDS, location, nextStation));
                    }
                    if (lim.oppositeBackwards) {
                        commands.add(getCommand(startPos, AutoDirection.BACKWARDS, location, nextStation));
                    }
                }
            }
        }
        return commands;

    }

    public static ArrayList<GZCommand> getAllPaths() {
        ArrayList<GZCommand> allCommands = new ArrayList<GZCommand>();

        for (StartingPosition startPosition : AllStartingPositions) {
            for (ScoringPosition scorePosition : AllScoringPositions) {
                for (ScoringSide scoringSide : AllScoringSides) {
                    for (FeederStation feederStation : AllFeederStations) {
                        // System.out.println(
                        // startPosition + "\t" + scorePosition + "\t" + scoringSide + "\t" +
                        // feederStation);
                        allCommands.addAll(getCommands(startPosition, new ScoringLocation(scorePosition, scoringSide),
                                feederStation));
                    }

                }
                // For every scoring position
            }

            // For every starting position
        }
        return allCommands;
    }

    public static FeederStation getFeederStationFromScoringSide(ScoringSide side) {
        if (side == ScoringSide.LEFT)
            return FeederStation.LEFT;

        return FeederStation.RIGHT;
    }

    public static GZCommand getCommand(final StartingPosition startPos, final AutoDirection direction,
            final ScoringLocation scoringLocation, final FeederStation nextStation) {
        GamePiece gamePiece = Auton.getInstance().isAutoPieceHatch() ? GamePiece.HATCH_PANEL : GamePiece.CARGO;
        GZCommandGroup com = new GZCommandGroup() {
            {
                // this.add(new GoToHeight(Heights.Home));
                {
                    Command prepForScore = prepForScoring(scoringLocation, gamePiece);

                    // Drive first path
                    GZCommandGroup driveOne = new GZCommandGroup();

                    // Parallel if we have a score command
                    driveOne.resetDrivePaths(getFirstPath(startPos, scoringLocation), prepForScore != null);

                    // While waiting to prep superstructure
                    if (prepForScore != null)
                        driveOne.waitForMarkerThen(prepForScore);

                    this.add(driveOne);
                }

                {
                    Command score = getScoringCommand(scoringLocation, gamePiece);
                    if (score != null)
                        add(score);
                }
                {
                    GZCommandGroup driveTwo = new GZCommandGroup();
                    Command prepForFeeder = prepForFeederStation();

                    driveTwo.drivePaths(getScoredPosToFeederStation(scoringLocation, nextStation),
                            prepForFeeder != null);

                    if (prepForFeeder != null)
                        driveTwo.waitForMarkerThen(prepForFeeder);
                    this.add(driveTwo);
                }

                {
                    Command retrieve = retrieveFromFeederStation();
                    if (retrieve != null)
                        add(retrieve);
                }

                // add(new WaitForButtonBoardInput());
            }
        };

        GZCommand ret = new GZCommand(
                startPos.toString() + (direction == AutoDirection.BACKWARDS ? " (Backwards) " : "") + " --> "
                        + scoringLocation.toString() + " --> " + nextStation.toString(),
                () -> com);
        ret.setFeederStation(nextStation);

        return ret;
    }

    public static synchronized GZCommand getCommandFromFeederStation(ScoringLocation location) {
        GZCommandGroup ret = new GZCommandGroup();

        {
            GZCommandGroup driveThree = new GZCommandGroup();
            driveThree.drivePaths(getFeederStationToSecondPlacement(mFeederStation, location));
            ret.add(driveThree);
        }

        ret.add(getScoringCommand(location, GamePiece.HATCH_PANEL));

        String name = mFeederStation.toString() + " --> " + location.toString();

        GZCommand out = new GZCommand(name, () -> ret);
        return out;
    }

}
