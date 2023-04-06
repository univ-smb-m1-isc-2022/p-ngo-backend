package com.ygdrazil.pingo.pingobackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "_bingo_saves")
public class BingoSave {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String gridUrlCode;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(nullable = false)
    private String gridCompletion;
}
