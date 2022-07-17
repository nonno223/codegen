package com.codegenerator;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.*;

import static java.util.Objects.isNull;

public class BuilderValue {

    public static void main(String[] args) {
        Object object = new Object();

        System.out.println(printFieldsDummy(object));
    }

    static String printFieldsDummy(Object object) {
        return BuilderGenerator.printFields(object, field -> writeValueType(field, object));
    }

    // region helpers
    static String writeValueType(Field field, Object obj) {
        field.setAccessible(true);
        try {
            Class<?> type = field.getType();
            if (shouldRecurse(type) && !isNull(field.get(obj))) {
                //      System.out.println(type);
                printFieldsDummy(field.get(obj));
                return "null";
            } else if (isNull(field.get(obj))) {
                return "null";
            }
            return BuilderGenerator.getFieldValue(field, field.get(obj), false);
        } catch (IllegalAccessException e) {
            return "null";
        }
    }


     static boolean shouldRecurse(Class<?> type) {
        return !type.isEnum()
               && !type.isPrimitive()
               && !type.isAssignableFrom(String.class)
               && !type.isAssignableFrom(Boolean.class)
               && !type.isAssignableFrom(BigDecimal.class)
               && !type.isAssignableFrom(UUID.class)
               && !type.isAssignableFrom(Long.class)
               && !type.isAssignableFrom(LocalDate.class)
               && !type.isAssignableFrom(Instant.class)
               && !type.isAssignableFrom(Integer.class)
               && !type.isAssignableFrom(Double.class)
               && !type.isAssignableFrom(OffsetDateTime.class)
               && !type.isAssignableFrom(OffsetTime.class)
               && !type.isAssignableFrom(Map.class)
               && !type.isAssignableFrom(Set.class)
               && !type.isAssignableFrom(List.class)
               && !type.isAssignableFrom(Collection.class);
    }
    // endregion
}
