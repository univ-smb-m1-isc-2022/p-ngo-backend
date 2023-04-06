package com.ygdrazil.pingo.pingobackend.services;

import com.ygdrazil.pingo.pingobackend.models.BingoGrid;
import com.ygdrazil.pingo.pingobackend.models.User;
import com.ygdrazil.pingo.pingobackend.repositories.BingoRepository;
import com.ygdrazil.pingo.pingobackend.requestObjects.CreateBingoGridRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BingoService {

    private final BingoRepository repository;

    public Optional<BingoGrid> find(Long bingoId) {
        return repository.findById(bingoId);
    }

    public Optional<BingoGrid> findByUrlCode(String urlCode) {
        return repository.findBingoGridByUrlCode(urlCode);
    }

    public List<BingoGrid> findAll() {
        return repository.findAll();
    }

    public List<BingoGrid> findAllByUser(User user) {
        return repository.findAllByUser(user);
    }

    public Optional<BingoGrid> insert(CreateBingoGridRequest request, User author) {

        Optional<BingoGrid> existingGrid = repository.findBingoGridByName(request.getName());

        if(existingGrid.isPresent())
            return Optional.empty();

        String urlCode = DigestUtils.sha1Hex(request.getName());

        BingoGrid bingoGrid = BingoGrid.builder()
                .name(request.getName())
                .urlCode(urlCode)
                .user(author)
                .gridData(request.getGridData())
                .dim(request.getDim())
                .build();

        return Optional.of(repository.save(bingoGrid));
    }

    public BingoGrid modify(BingoGrid modifiedBingoGrid, CreateBingoGridRequest body) {
        modifiedBingoGrid.setName(body.getName());
        modifiedBingoGrid.setGridData(body.getGridData());

        return repository.save(modifiedBingoGrid);
    }

    public void delete(BingoGrid deletedBingoGrid) {
        repository.delete(deletedBingoGrid);
    }
}
