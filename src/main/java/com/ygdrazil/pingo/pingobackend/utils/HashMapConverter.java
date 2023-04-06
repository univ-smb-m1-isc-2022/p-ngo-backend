package com.ygdrazil.pingo.pingobackend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HashMapConverter<K,V> implements AttributeConverter<Map<K, V>, String> {


    private final static ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(Map<K, V> map) {

        String mapJson = null;
        try {
            mapJson = objectMapper.writeValueAsString(map);
        } catch (final JsonProcessingException ignored) {

        }

        return mapJson;
    }

    @Override
    public Map<K, V> convertToEntityAttribute(String mapJson) {

        Map<K, V> map = null;
        try {
            map = objectMapper.readValue(mapJson,
                    new TypeReference<HashMap<K, V>>() {});
        } catch (final IOException ignored) {

        }

        return map;
    }
}