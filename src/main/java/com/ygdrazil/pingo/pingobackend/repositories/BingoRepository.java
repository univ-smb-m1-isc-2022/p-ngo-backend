package com.ygdrazil.pingo.pingobackend.repositories;

import com.ygdrazil.pingo.pingobackend.models.BingoGrid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BingoRepository extends JpaRepository<BingoGrid, Long> {
    List<BingoGrid> findAll();
}
