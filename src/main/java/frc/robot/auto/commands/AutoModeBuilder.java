package frc.robot.auto.commands;

import java.util.ArrayList;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.GZOI;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.paths.center.Center_CS_Bay_1_Left;
import frc.robot.auto.commands.paths.center.Center_CS_Bay_2_Left;
import frc.robot.auto.commands.paths.center.Center_CS_Bay_3_Left;
import frc.robot.auto.commands.paths.center.Center_CS_Face_Left;
import frc.robot.auto.commands.paths.left.Left_CS_Bay_1_Opp;
import frc.robot.auto.commands.paths.left.Left_CS_Bay_1_Same;
import frc.robot.auto.commands.paths.left.Left_CS_Bay_2_Opp;
import frc.robot.auto.commands.paths.left.Left_CS_Bay_2_Same;
import frc.robot.auto.commands.paths.left.Left_CS_Bay_3_Opp;
import frc.robot.auto.commands.paths.left.Left_CS_Bay_3_Same;
import frc.robot.auto.commands.paths.left.Left_CS_Face_Opp;
import frc.robot.auto.commands.paths.left.Left_CS_Face_Same;
import frc.robot.auto.commands.paths.to_feeder_station.CS_Face_Turn_Around_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.CS_Face_Turn_Around_Same;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_1_Turn_Around;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_2_Turn_Around_1;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_2_Turn_Around_2;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_3_Turn_Around_1;
import frc.robot.auto.commands.paths.to_feeder_station.Left_CS_Bay_3_Turn_Around_2;
import frc.robot.auto.commands.paths.to_feeder_station.To_Feeder_Station_Same;
import frc.robot.auto.commands.paths.to_feeder_station.To_Feeder_Station_Same_Shallow;
import frc.robot.util.GZCommand;
import frc.robot.util.GZCommandGroup;

public class AutoModeBuilder {

    private static final Supplier<GamePiece> mGamePieceSupplier = () -> GZOI.getInstance().getSafteyKey()
            ? GamePiece.CARGO
            : GamePiece.HATCH_PANEL;

    public enum StartingPosition {
        LEFT(true, false, "Left"), CENTER(false, false, "Center"), RIGHT(true, true, "Right");

        private final boolean onLeft;
        private final boolean onRight;
        private final String name;

        private StartingPosition(boolean onLeft, boolean onRight, String name) {
            this.onLeft = onLeft;
            this.onRight = onRight;
            this.name = name;
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
            return this.side.toString() + " " + this.pos.toString();
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

        default:
            return null;
        }

    }

    public static ArrayList<Command> getScoringCommand(ScoringLocation location, GamePiece gamepiece) {
        switch (location.pos) {
        case CARGO_SHIP_BAY_1:
            break;
        }

        return null;
    }

    public static ArrayList<Command> prepForScoring(ScoringLocation location, GamePiece gamepiece) {
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
        }

        return null;
    }

    public static ArrayList<Command> prepForFeederStation() {
        return null;
    }

    public static ArrayList<Command> retrieveFromFeederStation() {
        return null;
    }

    public static GZCommand getCommand(final StartingPosition startPos, final ScoringLocation location,
            final FeederStation nextStation) {

        return getCommand(startPos, location, nextStation, mGamePieceSupplier);
    }

    public static GZCommand getCommand(final StartingPosition startPos, final ScoringLocation scoringLocation,
            final FeederStation nextStation, final Supplier<GamePiece> gamePiece) {

        GZCommandGroup com = new GZCommandGroup() {
            {
                {
                    ArrayList<Command> prepForScore = prepForScoring(scoringLocation, gamePiece.get());

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
                    ArrayList<Command> score = getScoringCommand(scoringLocation, gamePiece.get());
                    if (score != null)
                        add(score);
                }
                {
                    GZCommandGroup driveTwo = new GZCommandGroup();
                    ArrayList<Command> prepForFeeder = prepForFeederStation();

                    driveTwo.drivePaths(getScoredPosToFeederStation(scoringLocation, nextStation),
                            prepForFeeder != null);

                    if (prepForFeeder != null)
                        driveTwo.waitForMarkerThen(prepForFeeder);
                    this.add(driveTwo);
                }

                {
                    ArrayList<Command> retrieve = retrieveFromFeederStation();
                    if (retrieve != null)
                        add(retrieve);
                }
            }
        };

        GZCommand ret = new GZCommand(startPos.name + " --> " + scoringLocation.toString() + " --> " + nextStation.toString(), () -> com);
        return ret;
    }

}