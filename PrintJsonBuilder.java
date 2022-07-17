package com.codegenerator;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import java.io.IOException;
import java.io.InputStream;

import static com.codegenerator.BuilderValue.writeValueType;

public class PrintJsonBuilder {

    private static final ObjectMapper objectMapper = createObjectMapper();

    public static void main(String[] args) throws IOException {
        InputStream file = PrintJsonBuilder.class.getResourceAsStream("./json/print.json");

        Object[] objects = objectMapper.reader()
                                       .readValue(file, Object[].class);

        for (Object object : objects) {
            System.out.println(printFieldsDummy(object));
        }
    }

    static String printFieldsDummy(Object object) {
        return BuilderGenerator.printFieldsBuilderOnly(object, field -> writeValueType(field, object));
    }

    // region helpers
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        return mapper;
    }
    // endregion
}
