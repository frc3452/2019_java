package frc.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class FirstTest {

    @Test
    public void test() {
        double expected;
        double actual;
        double delta;
        assertEquals(expected, actual, delta, "Expected " + expected + " must be within " + delta + " of " + actual);
    }
}