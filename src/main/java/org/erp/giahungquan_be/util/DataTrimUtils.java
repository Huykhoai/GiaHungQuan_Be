package org.erp.giahungquan_be.util;

import java.lang.reflect.Field;

public class DataTrimUtils {

    public static void trimStringFields(Object object) {
        if (object == null) return;

        Class<?> clazz = object.getClass();

        while (clazz != null && !clazz.getName().startsWith("java.lang")) {
            Field[] fields = clazz.getDeclaredFields();
            
            for (Field field : fields) {
                if (field.getType().equals(String.class)) {
                    try {
                        field.setAccessible(true);
                        String value = (String) field.get(object);

                        if (value != null) {
                            field.set(object, value.trim());
                        }
                    } catch (IllegalAccessException e) {
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
