package com.codegenerator;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public class BuilderValue {

    public static void main(String[] args) {
        Object object = new Object();

        System.out.println(printFieldsDummy(object));
    }

    static String printFieldsDummy(Object object) {
        return printFields(object, field -> writeValueType(field,object));
    }

    // region helpers
    static String writeValueType(Field field, Object obj) {
        field.setAccessible(true);
        try {
            Class<?> type = field.getType();
            if (!type.isEnum()
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
                && !isNull(field.get(obj))) {
                printFieldsDummy(field.get(obj));
                return "null";
            }
            return getFieldValue(field, field.get(obj), false);
        } catch (IllegalAccessException e) {
            return "null";
        }
    }
    // endregion
}
