package com.codegenerator;


import java.lang.reflect.Field;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;

public class Mapstruct {

    public static void main(String[] args) {
        Object target = new Object();
        Object source = new Object();

        System.out.println(printFieldsDummy(target, source));
    }

    static String printFieldsDummy(Object target, Object source) {
        return printMapFields(target, source);
    }

    // region helpers
    static String printMapFields(Object target, Object source) {
        final List<Field>   fieldsTarget = BuilderGenerator.getAllFields(target.getClass());
        final List<Field>   fieldsSource = BuilderGenerator.getAllFields(source.getClass());
        final StringBuilder sb           = new StringBuilder();
        for (Field targetField : fieldsTarget) {
            if (!isStatic(targetField.getModifiers())) {
                sb.append("@Mapping(target = \"")
                  .append(targetField.getName())
                  .append("\", source = \"")
                  .append(findSameNameField(fieldsSource, targetField.getName()))
                  .append("\")\n");
            }
        }
        return sb.toString();
    }

    static String findSameNameField(final List<Field> fields, final String name) {
        return fields.stream()
                     .filter(field -> field.getName()
                                           .equals(name))
                     .findFirst()
                     .map(Field::getName)
                     .orElse("");
    }
    // endregion
}
