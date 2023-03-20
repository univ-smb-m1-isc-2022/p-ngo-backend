package com.ygdrazil.pingo.pingobackend.models;

import com.ygdrazil.pingo.pingobackend.utils.HashMapConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "_bingo_grids")
public class BingoGrid {

    @Id
    @SequenceGenerator(name = "bingo_grids_sequence", sequenceName = "bingo_grids_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bingo_grids_sequence")
    private Long id;

    @Column(nullable = false, length=50, unique = true)
    private String name;

    @Column
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> gridData = new HashMap<>();
}
