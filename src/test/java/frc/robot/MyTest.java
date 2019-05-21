package frc.robot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MyTest {

    @Test
    public void go() {
        assertTrue(0 < 5);
    }

    @Test
    public void test() {

        double expected, actual, delta;
        delta = 5;

        expected = 1;
        actual = 6;
        assertEquals("Should be near", expected, actual, delta);
        // assertEquals("Should be equal", expected, actual);
    }

    @Test
    public void checkTime() {

    }

}