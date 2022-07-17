package com.codegenerator;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;

import static java.lang.reflect.Modifier.isStatic;

public class JsonPath {

    public static void main(String[] args) {
        System.out.println(printJsonPathFields(new Object()));
    }

    // region helpers
    static String printJsonPathFields(Object object) {
        final List<Field>   fields    = BuilderGenerator.getAllFields(object.getClass());
        final StringBuilder sb        = new StringBuilder();
        int                 fieldsNum = 0;
        for (Field field : fields) {
            if (!isStatic(field.getModifiers())) {
                sb.append(".andExpect(jsonPath(prefix + \"")
                  .append(".")
                  .append(field.getName())
                  .append("\").value(object.get")
                  .append(field.getName().substring(0, 1).toUpperCase())
                  .append(field.getName().substring(1))
                  .append("()")
                  .append(getFieldJson(field.getType()))
                  .append("))\n");
                fieldsNum++;
            }
        }
        sb.append(".andExpect(jsonPath(prefix + \".length()\").value(")
          .append(fieldsNum)
          .append("))");
        return sb.toString();
    }

    private static String getFieldJson(Class<?> type) {
        if (type.isEnum()
            || type.isAssignableFrom(UUID.class)) {
            return ".toString()";
        }
        if (type.isAssignableFrom(Long.class)) {
            return ".intValue()";
        }
        if (type.isAssignableFrom(BigDecimal.class)) {
            return ".doubleValue()";
        }
        if (type.isAssignableFrom(LocalDate.class)
            || type.isAssignableFrom(Instant.class)
            || type.isAssignableFrom(OffsetDateTime.class)
            || type.isAssignableFrom(OffsetTime.class)) {
            return ".exists()";
        }
        return "";
    }

    // endregion
}
