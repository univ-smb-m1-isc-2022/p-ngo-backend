package com.ygdrazil.pingo.pingobackend.responseObjects;

import com.ygdrazil.pingo.pingobackend.models.BingoGrid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BingoResponse {
    private Long id;
    private Long authorId;
    private String urlCode;
    private String name;
    private int dim;
    private List<String> gridData;

    public BingoResponse(BingoGrid grid) {
        id = grid.getId();
        authorId = grid.getUser().getId();
        urlCode = grid.getUrlCode();
        name = grid.getName();
        dim = grid.getDim();
        gridData = grid.getGridData();
    }

}
