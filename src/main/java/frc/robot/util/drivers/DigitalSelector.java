package frc.robot.util.drivers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.DigitalInput;

public class DigitalSelector {

    private final int port1, port2, port3, port4;
    private GZDigitalInput m1 = null, m2 = null, m3 = null, m4 = null;

    private Map<Integer, GZDigitalInput> map = new HashMap<Integer, GZDigitalInput>();
    private String mName;

    public DigitalSelector(DigitalSelectorConstants constants) {
        this(constants.name, constants.port1, constants.port2, constants.port3, constants.port4);
    }

    private DigitalSelector(String name, int port1, int port2, int port3, int port4) {
        mName = name;

        this.port1 = port1;
        this.port2 = port2;
        this.port3 = port3;
        this.port4 = port4;

        try {
            m1 = new GZDigitalInput(this.port1);
            m2 = new GZDigitalInput(this.port2);
            m3 = new GZDigitalInput(this.port3);
            m4 = new GZDigitalInput(this.port4);

            map.put(1, m1);
            map.put(2, m2);
            map.put(3, m3);
            map.put(4, m4);
        } catch (Exception e) {
            System.out.println("ERROR DigitalSelector" + name + " could not be created!");

            m1 = null;
            m2 = null;
            m3 = null;
            m4 = null;

            e.printStackTrace();
        }
    }

    public static int get(DigitalSelector tensSelector, DigitalSelector onesSelector) {
        if (tensSelector == null || onesSelector == null)
            return -1;

        int ret;

        int tensValue = tensSelector.get();
        int onesValue = onesSelector.get();

        if (tensValue == -1 || onesValue == -1)
            return -1;

        ret = (tensValue * 10) + onesValue;

        // Saftey check, shouldn't be possible but just in case
        if (ret < 0 || ret > 99)
            return -1;

        return ret;
    }

    public int get() {
        if (areAnyNull())
            return -1;

        // Datasheet found at
        // http://www.ia.omron.com/data_pdf/cat/a7bs_a7bl_ds_e_6_2_csm25.pdf
        if (none())
            return 0;
        else if (are(1))
            return 1;
        else if (are(2))
            return 2;
        else if (are(1, 2))
            return 3;
        else if (are(3))
            return 4;
        else if (are(1, 3))
            return 5;
        else if (are(2, 3))
            return 6;
        else if (are(1, 2, 3))
            return 7;
        else if (are(4))
            return 8;
        else if (are(1, 4))
            return 9;

        return -1;
    }

    private boolean none() {
        boolean ret = false;

        for (DigitalInput i : map.values())
            ret |= i.get();

        return !ret;
    }

    private boolean are(int... selectors) {
        boolean ret = true;

        for (Integer a : selectors) {
            if (map.containsKey(a))
                if (map.get(a).get())
                    ret &= true;
                else
                    ret = false;
            else
                ret = false;
        }

        return ret;
    }

    private boolean areAnyNull(DigitalInput... list) {
        boolean ret = false;
        for (DigitalInput i : list)
            ret |= i == null;
        return ret;

    }

    private boolean areAnyNull() {
        return areAnyNull(m1, m2, m3, m4);
    }

    @Override
    public String toString() {
        return mName;
    }

    public static class DigitalSelectorConstants {
        private final static int PORT_MIN = 0;
        private final static int PORT_MAX = 9;

        public final String name;
        public final int port1, port2, port3, port4;

        public DigitalSelectorConstants(String name, int port1, int port2, int port3, int port4) {
            this.name = name;
            this.port1 = port1;
            this.port2 = port2;
            this.port3 = port3;
            this.port4 = port4;

            ArrayList<Integer> ports = new ArrayList<Integer>();
            ports.addAll(Arrays.asList(this.port1, this.port2, this.port3, this.port4));

            int portNum = 1;
            for (int port : ports) {
                if (port < PORT_MIN || port > PORT_MAX)
                    throw new IllegalArgumentException("Port " + portNum + " cannot be on port " + port1 + ". (Must be between "
                            + PORT_MIN + " and " + PORT_MAX + ")");
                portNum++;
            }

        }
    }
}