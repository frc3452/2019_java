package frc.robot.util.drivers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DigitalSelector {

    private final int port1, port2, port3, port4;
    private GZDigitalInput m1 = null, m2 = null, m3 = null, m4 = null;

    private final boolean invert;

    private Map<Integer, GZDigitalInput> map = new HashMap<Integer, GZDigitalInput>();
    private String mName;

    public DigitalSelector(DigitalSelectorConstants constants) {
        this(constants.name, constants.invert, constants.portA, constants.portB, constants.portC, constants.portD);
    }

    private DigitalSelector(String name, boolean invert, int portA, int portB, int portC, int portD) {
        mName = name;

        this.port1 = portA;
        this.port2 = portB;
        this.port3 = portC;
        this.port4 = portD;
        this.invert = invert;

        try {
            m1 = new GZDigitalInput(this.port1, this.invert);
            m2 = new GZDigitalInput(this.port2, this.invert);
            m3 = new GZDigitalInput(this.port3, this.invert);
            m4 = new GZDigitalInput(this.port4, this.invert);

            m1.get();

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

        for (GZDigitalInput i : map.values())
            ret |= i.get();

        return !ret;
    }

    private boolean are(Integer... selectors) {
        boolean ret = true;
        List<Integer> list = Arrays.asList(selectors);

        for (int i = 1; i <= 4 && ret; i++) {
            if (list.contains(i)) {
                ret &= map.get(i).get();
            } else {
                ret &= !map.get(i).get();
            }
        }
        return ret;
    }

    private boolean areAnyNull(GZDigitalInput... list) {
        boolean ret = false;
        for (GZDigitalInput i : list)
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

    public void print() {
        String out = "DigitalSelector: " + toString();
        if (areAnyNull()) {
            out = " has some null inputs (A,B,C,D): " + (m1 == null) + "\t" + (m2 == null) + "\t" + (m3 == null) + "\t"
                    + (m4 == null);
            System.out.println(out);
            return;
        }

        out = "\tValue: " + this.get() + "\tIndividual Values: "
                + (m1.get() + "\t" + m2.get() + "\t" + m3.get() + "\t" + m4.get());
        System.out.println(out);
    }

    public static class DigitalSelectorConstants {
        private final static int PORT_MIN = 0;
        private final static int PORT_MAX = 9;

        public final boolean invert;
        public final String name;
        public final int portA, portB, portC, portD;

        public DigitalSelectorConstants(String name, int portA, int portB, int portC, int portD) {
            this(name, false, portA, portB, portC, portD);
        }

        public DigitalSelectorConstants(String name, boolean invert, int portA, int portB, int portC, int portD) {
            this.name = name;
            this.portA = portA;
            this.portB = portB;
            this.portC = portC;
            this.portD = portD;
            this.invert = invert;

            ArrayList<Integer> ports = new ArrayList<Integer>();
            ports.addAll(Arrays.asList(this.portA, this.portB, this.portC, this.portD));

            int portNum = 1;
            for (int port : ports) {
                if (port < PORT_MIN || port > PORT_MAX)
                    throw new IllegalArgumentException("Port " + portNum + " cannot be on port " + portA
                            + ". (Must be between " + PORT_MIN + " and " + PORT_MAX + ")");
                portNum++;
            }

        }
    }
}