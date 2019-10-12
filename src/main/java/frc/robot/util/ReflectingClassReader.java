package frc.robot.util;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

public class ReflectingClassReader<T> {

    public final String value;
    private DecimalFormat df = new DecimalFormat("#0.00");

    public ReflectingClassReader(Class<T> typeClass, T... variables) {
        String out;

        Field[] mFields = typeClass.getFields();
        // Write field names.
        StringBuffer line = new StringBuffer();
        for (Field field : mFields) {
            line.append(field.getName() + ":");
            for (T variable : variables) {
                line.append(" [");
                try {
                    final Object o = field.get(variable);

                    String valueOfObject = o.toString();
                    try {
                        double itsANumber = Double.parseDouble(o.toString());
                        valueOfObject = df.format(itsANumber);
                    } catch (NumberFormatException n) {
                    }
                    line.append(valueOfObject);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                line.append("]");
            }
            line.append("\n");
        }

        out = line.toString();
        value = out;
    }

    @Override
    public String toString() {
        return value;
    }
}
