package com.ygdrazil.pingo.pingobackend.repositories;

import com.ygdrazil.pingo.pingobackend.models.BingoGrid;
import com.ygdrazil.pingo.pingobackend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BingoRepository extends JpaRepository<BingoGrid, Long> {
    Optional<BingoGrid> findBingoGridByName(String name);
    Optional<BingoGrid> findBingoGridByUrlCode(String urlCode);

    List<BingoGrid> findAllByUser(User user);
}
