package com.ygdrazil.pingo.pingobackend.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WSMessage {
    private String from;
    private String text;
}
