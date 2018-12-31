package frc.robot.poofs.geometry;

import frc.robot.poofs.util.CSVWritable;
import frc.robot.poofs.util.Interpolable;

public interface State<S> extends Interpolable<S>, CSVWritable {
    double distance(final S other);

    boolean equals(final Object other);

    String toString();

    String toCSV();
}
