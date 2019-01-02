package frc.robot.util.drivers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wpi.first.wpilibj.DigitalInput;

public class DigitalSelector {

    private GZDigitalInput m1 = null, m2 = null, m3 = null, m4 = null;

    private Map<Integer, GZDigitalInput> map = new HashMap<Integer, GZDigitalInput>();
    private String mName;

    public DigitalSelector(int port1, int port2, int port3, int port4) {
        this("Unspecified", port1, port2, port3, port4);
    }

    public DigitalSelector(String name, int port1, int port2, int port3, int port4) {
        mName = name;

        try {
            m1 = new GZDigitalInput(port1);
            m2 = new GZDigitalInput(port2);
            m3 = new GZDigitalInput(port3);
            m4 = new GZDigitalInput(port4);

            map.put(1, m1);
            map.put(2, m2);
            map.put(3, m3);
            map.put(4, m4);
        } catch (Exception e) {
            System.out.println("ERROR DigitalSelector" + name + " could not be created!");
            e.printStackTrace();
        }
    }

    public static int get(DigitalSelector a, DigitalSelector b)
    {
        int ret;

        int aVal = a.get();
        int bVal = b.get();

        if (aVal == -1 || bVal == -1)
            return -1;
        
        //First selector is tens place, second selector is ones place 
        ret = aVal * 10;
        ret += bVal;

        //Saftey check, shouldn't be possible but just in case
        if (ret < 0 || ret > 99)
            return -1;

        return ret;
    }

    public int get() {
        if (areAnyNull())
            return -1;

        //Datasheet found at http://www.ia.omron.com/data_pdf/cat/a7bs_a7bl_ds_e_6_2_csm25.pdf
        if (none())
            return 0;
        else if (are(1))
            return 1;
        else if (are(2))
            return 2;
        else if (are(1,2))
            return 3;
        else if (are(3))
            return 4;
        else if (are(1,3))
            return 5;
        else if (are(2,3))
            return 6;
        else if (are(1,2,3))
            return 7;
        else if (are(4))
            return 8;
        else if (are(1,4))
            return 9;

        return -1;
    }

    private boolean none()
    {
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

    private boolean areAnyNull(DigitalInput ...list) {
        boolean ret = false;
        for (DigitalInput i : list)
            ret |= i == null;
        return ret;

    }

    private boolean areAnyNull() {
        return areAnyNull(m1,m2,m3,m4);
    }

    @Override
    public String toString() {
        return mName;
    }
}