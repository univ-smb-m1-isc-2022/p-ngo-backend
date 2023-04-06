package com.ygdrazil.pingo.pingobackend.repositories;

import com.ygdrazil.pingo.pingobackend.models.BingoGrid;
import com.ygdrazil.pingo.pingobackend.models.BingoSave;
import com.ygdrazil.pingo.pingobackend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BingoSaveRepository extends JpaRepository<BingoSave, Long>  {
    Optional<BingoSave> findBingoSaveByUserIdAndGridUrlCode(Long user_id, String gridUrlCode);
}
