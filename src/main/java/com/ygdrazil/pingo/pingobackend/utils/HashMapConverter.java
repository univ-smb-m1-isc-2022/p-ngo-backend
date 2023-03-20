package com.ygdrazil.pingo.pingobackend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {


    private final static ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(Map<String, Object> map) {

        String mapJson = null;
        try {
            mapJson = objectMapper.writeValueAsString(map);
        } catch (final JsonProcessingException ignored) {

        }

        return mapJson;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String mapJson) {

        Map<String, Object> map = null;
        try {
            map = objectMapper.readValue(mapJson,
                    new TypeReference<HashMap<String, Object>>() {});
        } catch (final IOException ignored) {

        }

        return map;
    }
}