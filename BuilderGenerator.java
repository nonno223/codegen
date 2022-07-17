package com.codegenerator;

import lombok.val;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.*;
import java.util.function.Function;

import static java.lang.String.*;
import static java.lang.reflect.Modifier.isStatic;

public class BuilderGenerator {
    // region state
    static class StateWrapOne {
        private final BigDecimal bdInc   = BigDecimal.valueOf(0.01);
        private       BigDecimal bd      = BigDecimal.valueOf(0.99);
        private final Integer    intInc  = 1;
        private       Integer    integer = 0;
        private final Long       longInc = 1L;
        private       Long       lng     = 0L;
        private final Double     dblInc  = 0.1;
        private       Double     dbl     = 0.9;
        private final String     str     = "One";
        private final String     bool    = "true";
        private final int        enumIdx = 0;
        private final String     fixture = "Test.getFixtureOne()";
    }

    static class StateWrapTwo {
        private final BigDecimal bdInc   = BigDecimal.valueOf(0.01);
        private       BigDecimal bd      = BigDecimal.valueOf(2.99);
        private final Integer    intInc  = 2;
        private       Integer    integer = 0;
        private final Long       longInc = 2L;
        private       Long       lng     = 0L;
        private final Double     dblInc  = 0.2;
        private       Double     dbl     = 1.9;
        private final String     str     = "True";
        private final String     bool    = "false";
        private final int        enumIdx = 1;
        private final String     fixture = "Test.getFixtureTwo()";
    }

    // endregion
    private static final StateWrapOne state = new StateWrapOne();
//    private static final StateWrapTwo state = new StateWrapTwo();

    public static void main(String[] args) {
        Object object = new Object();

        System.out.println(printFieldsDummy(object));
    }

    static String printFieldsDummy(Object object) {
        return printFields(object, field -> getFieldValue(field, null, true));
    }

    static String printFields(Object object, Function<Field, String> value) {
        final List<Field>   fields = getAllFields(object.getClass());
        final StringBuilder sb     = new StringBuilder(object.getClass().getSimpleName() + ".builder()\n");
        for (Field field : fields) {
            if (!isStatic(field.getModifiers())) {
                sb.append(".").append(field.getName())
                  .append("(").append(value.apply(field)).append(")\n");
            }
        }
        sb.append(".build();\n");
        return sb.toString();
    }

    // region java field values
    static String getFieldValue(Field field, Object val, boolean useDefault) {
        final Class<?> type = field.getType();
        // region java.lang
        if (isType(type, String.class)) {
            return format("\"%s\"", getVal(field.getName() + state.str, val, useDefault));
        }
        if (isType(type, BigDecimal.class)) {
            state.bd = state.bd.add(state.bdInc);
            return format("BigDecimal.valueOf(%s)",
                          getVal(state.bd.toPlainString(), val, useDefault));
        }
        if (isType(type, UUID.class)) {
            state.bd = state.bd.add(state.bdInc);
            return format("UUID.fromString(\"%s\")",
                          getVal(UUID.randomUUID(), val, useDefault));
        }
        // endregion
        // region java.time
        if (isType(type, LocalDate.class)) {
            return format("LocalDate.parse(\"%s\")",
                          getVal(LocalDate.now()
                                          .minusDays(state.intInc), val, useDefault));
        }
        if (isType(type, Instant.class)) {
            return format("Instant.parse(\"%s\")",
                          getVal(Instant.now()
                                        .minusSeconds(state.longInc), val, useDefault));
        }
        if (isType(type, OffsetDateTime.class)) {
            return format("OffsetDateTime.parse(\"%s\")",
                          getVal(OffsetDateTime.now()
                                               .minusSeconds(state.longInc), val, useDefault));
        }
        if (isType(type, OffsetTime.class)) {
            return format("OffsetTime.parse(\"%s\")",
                          getVal(OffsetTime.now()
                                           .minusSeconds(state.longInc), val, useDefault));
        }
        // endregion
        // region primitive
        if (isType(type, Boolean.TYPE, Boolean.class)) {
            return getVal(state.bool, val, useDefault);
        }
        if (isType(type, Integer.TYPE, Integer.class)) {
            state.integer = state.integer + state.intInc;
            return getVal(state.integer, val, useDefault);
        }
        if (isType(type, Long.TYPE, Long.class)) {
            state.lng = state.lng + state.longInc;
            return getVal(state.lng, val, useDefault);
        }
        if (isType(type, Double.TYPE, Double.class)) {
            state.dbl = state.dbl + state.dblInc;
            return getVal(state.dbl, val, useDefault);
        }
        if (type.isEnum()) {
            Object[] ct     = type.getEnumConstants();
            String   prefix = type.getSimpleName() + ".";
            return getVal(prefix + ct[Math.max(state.enumIdx, ct.length - 1)],
                          prefix + val, useDefault);
        }
        // endregion
        return getPrjFieldValue(field);
    }

    // endregion
    // region project field values
    static String getPrjFieldValue(Field field) {
        final Class<?> type = field.getType();

        return type.getSimpleName() + state.fixture;
    }

    // endregion
    // region helpers
    static boolean isType(Class<?> type, Class<?> primitive, Class<?> cls) {
        return type.isAssignableFrom(primitive) || type.isAssignableFrom(cls);
    }

    static boolean isType(Class<?> type, Class<?> cls) {
        return type.isAssignableFrom(cls);
    }

    static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    static String getVal(Object def, Object val, boolean useDefault) {
        return useDefault ? String.valueOf(def) : String.valueOf(val);
    }
    // endregion
}
