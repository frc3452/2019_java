package frc.robot.auto.commands;

import java.util.ArrayList;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.GZOI;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.superstructure.GoToHeight;
import frc.robot.auto.commands.functions.superstructure.HomeElevator;
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
import frc.robot.auto.commands.paths.left.Left_Rocket_Mid_Opp;
import frc.robot.auto.commands.paths.left.Left_Rocket_Mid_Same;
import frc.robot.auto.commands.paths.to_feeder_station.CS_Face_Turn_Around_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.CS_Face_Turn_Around_Same;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_1_Turn_Around;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_2_Turn_Around_1;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_2_Turn_Around_2;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_3_Turn_Around_1;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_3_Turn_Around_2;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Close_Turn_Around_2_If_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Close_Turn_Around_Same;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Far_Turn_Around_1;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Far_Turn_Around_2_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Far_Turn_Around_2_Same;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Mid_Turn_Around_2_If_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.Rocket_Mid_Turn_Around_Same;
import frc.robot.auto.commands.paths.to_feeder_station.To_Feeder_Station_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.To_Feeder_Station_Same_Shallow;
import frc.robot.subsystems.Superstructure.Actions;
import frc.robot.util.GZCommand;
import frc.robot.util.GZCommandGroup;

public class AutoModeBuilder {

    private static final Supplier<GamePiece> mGamePieceSupplier = () -> GZOI.getInstance().getSafteyKey()
            ? GamePiece.CARGO
            : GamePiece.HATCH_PANEL;

    public static final ArrayList<StartingPosition> AllStartingPositions = new ArrayList<StartingPosition>();
    public static final ArrayList<ScoringPosition> AllScoringPositions = new ArrayList<ScoringPosition>();
    public static final ArrayList<FeederStation> AllFeederStations = new ArrayList<FeederStation>();
    public static final ArrayList<ScoringSide> AllScoringSides = new ArrayList<ScoringSide>();

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
        CENTER(false, false, "Center"), LEFT(true, false, "Left"), RIGHT(true, true, "Right");

        private final boolean onLeft;
        private final boolean onRight;
        private final String name;

        private StartingPosition(boolean onLeft, boolean onRight, String name) {
            this.onLeft = onLeft;
            this.onRight = onRight;
            this.name = name;

            AllStartingPositions.add(this);
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
        ROCKET_MID("Rocket Middle Face", false), ROCKET_FAR("Rocket Far Face", false);

        public final String text;
        public final boolean cargoShip;

        private ScoringPosition(String text, boolean isCargoShip) {
            this.cargoShip = isCargoShip;
            this.text = text;

            AllScoringPositions.add(this);
        }

        private ScoringPosition(String text) {
            this(text, true);
        }

        @Override
        public String toString() {
            return text;
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

        default:
            return null;
        }

    }

    public static Command getScoringCommand(ScoringLocation location, GamePiece gamepiece) {
        GZCommandGroup ret = new GZCommandGroup();

        ret.tele();
        if (location.isOnCargoShip()) {
            switch (gamepiece) {
            case CARGO:
                ret.add(new GoToHeight(Heights.Cargo_1));
                ret.add(new HomeElevator());
                ret.add(new GoToHeight(Heights.Cargo_Ship));
                ret.add(new RunAction(Actions.THROW_CARGO));
                ret.tele();
                break;
            case HATCH_PANEL:
                ret.add(new GoToHeight(Heights.Cargo_1));
                ret.add(new HomeElevator());
                ret.add(new GoToHeight(Heights.HP_1));
                ret.add(new RunAction(Actions.SCORE_HATCH));
                ret.tele();
                break;
            }
        } else {
            // I guess we die?
        }

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
            if (feederSameSide(location, station)) {
                ret.add(new Left_CS_Bay_1_Turn_Around().get(location.side.onLeft));
                ret.add(new To_Feeder_Station_Same_Shallow().get(station.onLeft));
            } else {
                return null;
            }
            return ret;
        }

        case CARGO_SHIP_BAY_2: {
            ArrayList<PathContainer> ret = new ArrayList<>();
            if (feederSameSide(location, station)) {
                ret.add(new Left_CS_Bay_2_Turn_Around_1().get(location.side.onLeft));
                ret.add(new Left_CS_Bay_2_Turn_Around_2().get(location.side.onLeft));
                ret.add(new To_Feeder_Station_Same_Shallow().get(station.onLeft));
            } else {
                return null;
            }
            return ret;
        }

        case CARGO_SHIP_BAY_3: {
            ArrayList<PathContainer> ret = new ArrayList<>();
            if (feederSameSide(location, station)) {
                ret.add(new Left_CS_Bay_3_Turn_Around_1().get(location.side.onLeft));
                ret.add(new Left_CS_Bay_3_Turn_Around_2().get(location.side.onLeft));
                ret.add(new To_Feeder_Station_Same_Shallow().get(station.onLeft));
            } else {
                return null;
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
        }

        return null;
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
        ret.add(new RunAction(Actions.GRAB_HP_FROM_FEED));
        return ret;
    }

    public static GZCommand getCommand(final StartingPosition startPos, final ScoringLocation location,
            final FeederStation nextStation) {

        return getCommand(startPos, location, nextStation, mGamePieceSupplier);
    }

    public static ArrayList<GZCommand> getAllPaths() {
        ArrayList<GZCommand> allCommands = new ArrayList<GZCommand>();

        for (StartingPosition startPosition : AllStartingPositions) {

            for (ScoringPosition scorePosition : AllScoringPositions) {

                if (scorePosition.cargoShip) {

                    if (scorePosition == ScoringPosition.CARGO_SHIP_FACE) {

                        for (ScoringSide scoringSide : AllScoringSides) {

                            for (FeederStation feederStation : AllFeederStations) {
                                allCommands.add(getCommand(startPosition,
                                        new ScoringLocation(scorePosition, scoringSide), feederStation));
                            }
                        }

                        // If scoring on cargo ship face
                    } else {

                        for (ScoringSide scoringSide : AllScoringSides) {
                            allCommands.add(getCommand(startPosition, new ScoringLocation(scorePosition, scoringSide),
                                    getFeederStationFromScoringSide(scoringSide)));
                        }

                    }

                    // If we are scoring on cargo ship
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

    public static GZCommand getCommand(final StartingPosition startPos, final ScoringLocation scoringLocation,
            final FeederStation nextStation, final Supplier<GamePiece> gamePiece) {

        GZCommandGroup com = new GZCommandGroup() {
            {
                this.add(new GoToHeight(Heights.Home));

                {
                    Command prepForScore = prepForScoring(scoringLocation, gamePiece.get());

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
                    Command score = getScoringCommand(scoringLocation, gamePiece.get());
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

                // {
                // GZCommandGroup driveThree = new GZCommandGroup();
                // driveThree.drivePaths(getFeederStationToSecondPlacement(nextStation,
                // scoringLocation));
                // }
            }
        };

        GZCommand ret = new GZCommand(
                startPos.name + " --> " + scoringLocation.toString() + " --> " + nextStation.toString(), () -> com);
        return ret;
    }

}