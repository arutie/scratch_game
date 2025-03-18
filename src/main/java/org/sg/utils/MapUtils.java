package org.sg.utils;

import java.util.Map;

public class MapUtils {
    public static Object getMissingOrNull(Map<String, Object> m, String key) {
        if(m.containsKey(key)) {
            return m.get(key);
        }
        return null;
    }

    public static String getMissingOrBlank(Map<String, Object> m, String key) {
        if(m.containsKey(key)) {
            return m.get(key).toString();
        }
        return "";
    }

    public static int getInt(Map<String, Object> m, String key) {
        if(m.containsKey(key)) {
            return ConvertUtils.toInt(m.get(key), 0);
        }
        return 0;
    }

    public static int getInt(Map<String, Object> m, String key, int def) {
        if(m.containsKey(key)) {
            return ConvertUtils.toInt(m.get(key), def);
        }
        return def;
    }

    public static double getDouble(Map<String, Object> m, String key, double def) {
        if(m.containsKey(key)) {
            return ConvertUtils.toDouble(m.get(key), def);
        }
        return def;
    }


}
