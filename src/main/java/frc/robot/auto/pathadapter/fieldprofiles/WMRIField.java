package frc.robot.auto.pathadapter.fieldprofiles;

public class WMRIField extends FieldProfile {


    HeightsContainer heights;

    public WMRIField() {
        //No autonomous's  now, we dont have field offset values

        final HeightsContainer offsetField = new PracticeField().getElevatorHeights();
        var builder = new HeightsContainer.HeightsBuilder(offsetField);

        //you can either set the height here, like this
        //builder.cargo1 = 40123;

        //or just add to it like this to apply offset
        //        builder.cargo1 += 2.0;

        heights = builder.build();
    }

    @Override

    public HeightsContainer getElevatorHeights() {
        return heights;
    }
}
