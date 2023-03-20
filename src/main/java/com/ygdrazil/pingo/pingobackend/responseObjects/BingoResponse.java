package com.ygdrazil.pingo.pingobackend.responseObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BingoResponse {
    private Long id;
    private String name;
    private Map<String, Object> gridData;

}
