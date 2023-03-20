package com.ygdrazil.pingo.pingobackend.requestObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBingoGridRequest {
    private String name;
    private Map<String, Object> gridData;
}
