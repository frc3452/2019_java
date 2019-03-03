package frc.robot.auto.commands;

import java.util.ArrayList;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.GZOI;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.paths.center.Center_CS_Bay_1_Left;
import frc.robot.auto.commands.paths.center.Center_CS_Face_Left;
import frc.robot.auto.commands.paths.left.Left_CS_Bay_1_Opp;
import frc.robot.auto.commands.paths.left.Left_CS_Bay_1_Same;
import frc.robot.auto.commands.paths.left.Left_CS_Face_Opp;
import frc.robot.auto.commands.paths.left.Left_CS_Face_Same;
import frc.robot.auto.commands.paths.to_feeder_station.CS_Face_Turn_Around_Opp;
import frc.robot.auto.commands.paths.to_feeder_station.CS_Face_Turn_Around_Same;
import frc.robot.auto.commands.paths.to_feeder_station.To_Feeder_Station_Same;
import frc.robot.util.GZCommand;
import frc.robot.util.GZCommandGroup;

public class AutoModeBuilder {

    private static final Supplier<GamePiece> mGamePieceSupplier = () -> GZOI.getInstance().getSafteyKey()
            ? GamePiece.CARGO
            : GamePiece.HATCH_PANEL;

    public enum StartingPosition {
        LEFT(true, false), CENTER(false, false), RIGHT(true, true);

        private final boolean onLeft;
        private final boolean onRight;

        private StartingPosition(boolean onLeft, boolean onRight) {
            this.onLeft = onLeft;
            this.onRight = onRight;
        }
    }

    public class ScoringLocation {
        private final ScoringPosition pos;
        private final ScoringSide side;

        public ScoringLocation(ScoringPosition pos, ScoringSide side) {

            this.pos = pos;
            this.side = side;
        }
    }

    public enum ScoringPosition {
        CARGO_SHIP_FACE(), CARGO_SHIP_BAY_1(), CARGO_SHIP_BAY_2(), CARGO_SHIP_BAY_3(), ROCKET_NEAR, ROCKET_MID,
        ROCKET_FAR;

        private ScoringPosition() {
        }
    }

    public enum GamePiece {
        CARGO, HATCH_PANEL
    }

    public enum FeederStation {
        LEFT(true), RIGHT(false);
        private final boolean onLeft;
        private final boolean onRight;

        private FeederStation(boolean onLeft) {
            this.onLeft = onLeft;
            this.onRight = !this.onLeft;
        }
    }

    public enum ScoringSide {
        LEFT(true), RIGHT(false);
        private final boolean onLeft;
        private final boolean onRight;

        private ScoringSide(boolean onLeft) {
            this.onLeft = onLeft;
            this.onRight = !this.onLeft;
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
        case CARGO_SHIP_FACE:

            ArrayList<PathContainer> ret = new ArrayList<>();
            if (feederSameSide(location, station)) {
                ret.add(new CS_Face_Turn_Around_Same().get(location.side.onLeft));
            } else {
                ret.add(new CS_Face_Turn_Around_Opp().get(location.side.onLeft));
            }
            ret.add(new To_Feeder_Station_Same().get(station.onLeft));
            return ret;
        }
        return null;
    }

    public static Command prepForFeederStation() {
        return null;
    }

    public static Command retrieveFromFeederStation() {
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
                    // Drive first path
                    GZCommandGroup driveOne = new GZCommandGroup();
                    driveOne.resetDrivePathsAnd(getFirstPath(startPos, scoringLocation));

                    // While waiting to prep superstructure
                    driveOne.waitForMarkerThen(prepForScoring(scoringLocation, gamePiece.get()));

                    this.add(driveOne);
                }

                add(getScoringCommand(scoringLocation, gamePiece.get()));
                {
                    GZCommandGroup driveTwo = new GZCommandGroup();
                    driveTwo.drivePathsAnd(getScoredPosToFeederStation(scoringLocation, nextStation));
                    driveTwo.add(prepForFeederStation());
                    this.add(driveTwo);
                }
                add(retrieveFromFeederStation());
            }
        };

        GZCommand ret = new GZCommand("", () -> com);
        return ret;
    }

}