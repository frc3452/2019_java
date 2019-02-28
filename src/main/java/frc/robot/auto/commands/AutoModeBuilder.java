package frc.robot.auto.commands;

import java.util.ArrayList;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.GZOI;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.util.GZCommand;
import frc.robot.util.GZCommandGroup;

public class AutoModeBuilder {

    private static final Supplier<GamePiece> mGamePieceSupplier = () -> GZOI.getInstance().getSafteyKey()
            ? GamePiece.CARGO
            : GamePiece.HATCH_PANEL;

    public enum StartingPosition {
        LEFT, CENTER, RIGHT,
    }

    public class ScoringLocation {
        private final ScoringPosition mPos;
        private final ScoringSide mSide;

        public ScoringLocation(ScoringPosition pos, ScoringSide side) {

            this.mPos = pos;
            this.mSide = side;
        }
    }

    public enum ScoringPosition {
        CARGO_SHIP_FACE(true), CARGO_SHIP_BAY_1(true), CARGO_SHIP_BAY_2(true), CARGO_SHIP_BAY_3(true), ROCKET_NEAR,
        ROCKET_MID, ROCKET_FAR;

        public final boolean isCShip;

        private ScoringPosition() {
            this.isCShip = false;
        }

        private ScoringPosition(boolean cargoShip) {
            this.isCShip = cargoShip;
        }
    }

    public enum ScoringSide {
        LEFT, RIGHT
    }

    public enum GamePiece {
        CARGO, HATCH_PANEL
    }

    public enum FeederStation {
        LEFT, RIGHT
    }

    public static ArrayList<PathContainer> getFirstPath(StartingPosition startPos, ScoringLocation location) {
        switch (startPos) {
        case CENTER:
            break;
        case LEFT:
            break;
        case RIGHT:
            break;
        default:
        }
        return null;
    }

    public static ArrayList<Command> getScoringCommand(ScoringLocation location, GamePiece gamepiece) {
        switch (location.mPos) {
        case CARGO_SHIP_BAY_1:
            break;
        }

        return null;
    }

    public static ArrayList<Command> prepForScoring(ScoringLocation location, GamePiece gamepiece) {
        switch (location.mPos) {
        case CARGO_SHIP_BAY_1:
            break;
        }
        return null;
    }

    public static ArrayList<PathContainer> getScoredPosToFeederStation(final ScoringLocation location,
            final FeederStation station) {
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