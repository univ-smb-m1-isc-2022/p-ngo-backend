package com.ygdrazil.pingo.pingobackend.utils;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.List;

public class ListStringConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        return String.join(", ", stringList);
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        return Arrays.asList(s.split(", "));
    }
}
