package com.ygdrazil.pingo.pingobackend.models;

import com.ygdrazil.pingo.pingobackend.utils.ListStringConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "_bingo_grids")
public class BingoGrid {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String urlCode;

    @Column(nullable = false, length=50, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(nullable = false, name="user_id")
    private User user;

    @Column(nullable = false)
    private int dim;

    @Column(nullable = false)
    @Convert(converter = ListStringConverter.class)
    private List<String> gridData = List.of();
}
