package org.sg.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class ObjectUtils {
    public static boolean isArray(final Object object) {
        return object != null && object.getClass().isArray();
    }
    public static boolean isEmpty(final Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof CharSequence) {
            return ((CharSequence) object).length() == 0;
        }
        if (isArray(object)) {
            return Array.getLength(object) == 0;
        }
        if (object instanceof Collection<?>) {
            return ((Collection<?>) object).isEmpty();
        }
        if (object instanceof Map<?,?>) {
            return ((Map<?, ?>) object).isEmpty();
        }
        if (object instanceof Optional<?>) {
            // TODO Java 11 Use Optional#isEmpty()
            return ((Optional<?>) object).isEmpty();
        }
        return false;
    }
}
