package frc.robot.auto.pathadapter.fieldprofiles;

public class HeightsContainer {

    public final double hp_floor_grab;
    public final double hp1;
    public final double hp2;
    public final double hp3;
    public final double cargo_ship;
    public final double cargo1;
    public final double cargo2;
    public final double cargo3;
    public final double hp_feed_jog;
    public final double zero;
    public final double lowest_with_slides_out;
    public final double home;
    public final int ticks_per_inch;
    public final double hatch_place_jog;

    public HeightsContainer(double hp_floor_grab, double hp1, double hp2, double hp3, double cargo_ship, double cargo1, double cargo2, double cargo3, double hp_feed_jog, double zero, double lowest_with_slides_out, double home, int ticks_per_inch, double hatch_place_jog) {
        this.hp_floor_grab = hp_floor_grab;
        this.hp1 = hp1;
        this.hp2 = hp2;
        this.hp3 = hp3;
        this.cargo_ship = cargo_ship;
        this.cargo1 = cargo1;
        this.cargo2 = cargo2;
        this.cargo3 = cargo3;
        this.hp_feed_jog = hp_feed_jog;
        this.zero = zero;
        this.lowest_with_slides_out = lowest_with_slides_out;
        this.home = home;
        this.ticks_per_inch = ticks_per_inch;
        this.hatch_place_jog = hatch_place_jog;
    }

    public static class HeightsBuilder {
        public double hp_floor_grab;
        public double hp1;
        public double hp2;
        public double hp3;
        public double cargo_ship;
        public double cargo1;
        public double cargo2;
        public double cargo3;
        public double hp_feed_jog;
        public double zero;
        public double lowest_with_slides_out;
        public double home;
        public int ticks_per_inch;
        public double hatch_place_jog;

        public HeightsBuilder() {
        }

        public HeightsBuilder(HeightsBuilder other) {
            hp_floor_grab = other.hp_floor_grab;
            hp1 = other.hp1;
            hp2 = other.hp2;
            hp3 = other.hp3;
            cargo_ship = other.cargo_ship;
            cargo1 = other.cargo1;
            cargo2 = other.cargo2;
            cargo3 = other.cargo3;
            hp_feed_jog = other.hp_feed_jog;
            zero = other.zero;
            lowest_with_slides_out = other.lowest_with_slides_out;
            home = other.home;
            ticks_per_inch = other.ticks_per_inch;
            hatch_place_jog = other.hatch_place_jog;
        }

        public HeightsBuilder(HeightsContainer other) {
            hp_floor_grab = other.hp_floor_grab;
            hp1 = other.hp1;
            hp2 = other.hp2;
            hp3 = other.hp3;
            cargo_ship = other.cargo_ship;
            cargo1 = other.cargo1;
            cargo2 = other.cargo2;
            cargo3 = other.cargo3;
            hp_feed_jog = other.hp_feed_jog;
            zero = other.zero;
            lowest_with_slides_out = other.lowest_with_slides_out;
            home = other.home;
            ticks_per_inch = other.ticks_per_inch;
            hatch_place_jog = other.hatch_place_jog;
        }

        public HeightsContainer build() {
            return new HeightsContainer(hp_floor_grab, hp1, hp2, hp3, cargo_ship, cargo1, cargo2, cargo3, hp_feed_jog, zero, lowest_with_slides_out, home, ticks_per_inch, hatch_place_jog);
        }

    }
}