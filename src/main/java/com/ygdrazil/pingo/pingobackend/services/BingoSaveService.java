package com.ygdrazil.pingo.pingobackend.services;

import com.ygdrazil.pingo.pingobackend.models.BingoSave;
import com.ygdrazil.pingo.pingobackend.models.User;
import com.ygdrazil.pingo.pingobackend.repositories.BingoSaveRepository;
import com.ygdrazil.pingo.pingobackend.requestObjects.CreateBingoSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BingoSaveService {

    private final BingoSaveRepository repository;

    public Optional<BingoSave> findByUserAndUrlCode(Long userId, String urlCode) {
        return repository.findBingoSaveByUserIdAndGridUrlCode(userId, urlCode);
    }

    public Optional<BingoSave> insert(CreateBingoSaveRequest request, User user) {
        Optional<BingoSave> existingSave = repository.findBingoSaveByUserIdAndGridUrlCode(user.getId(), request.getUrlCode());

        if(existingSave.isPresent())
            return Optional.empty();

        BingoSave bingoSave = BingoSave.builder()
                .gridUrlCode(request.getUrlCode())
                .gridCompletion(request.getGridCompletion())
                .user(user)
                .build();

        return Optional.of(repository.save(bingoSave));
    }

    public Optional<BingoSave> modify(CreateBingoSaveRequest request, User user) {
        Optional<BingoSave> existingSave = repository.findBingoSaveByUserIdAndGridUrlCode(user.getId(), request.getUrlCode());

        if(existingSave.isEmpty())
            return Optional.empty();

        BingoSave bingoSave = existingSave.get();

        bingoSave.setGridCompletion(request.getGridCompletion());

        return Optional.of(repository.save(bingoSave));
    }
}
