package org.sg.utils;

public class ConvertUtils {
    public static double toDouble(Object o, double def) {
        try {
            if(o instanceof Double) {
                return (double) o;
            }
            if(o instanceof Integer) {
                return ((Integer)o).doubleValue();
            }
            if(o instanceof String) {
                return Double.parseDouble((String)o);
            }
        } catch(Exception ex) {
        }
        return def;
    }

    public static int toInt(Object o, int def) {
        try {
            if(o instanceof Double) {
                return ((Double) o).intValue();
            }
            if(o instanceof Integer) {
                return (int)o;
            }
            if(o instanceof String) {
                return Integer.parseInt((String)o);
            }
        } catch(Exception ex) {
        }
        return def;
    }
}
