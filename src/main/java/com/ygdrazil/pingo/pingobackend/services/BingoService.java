package com.ygdrazil.pingo.pingobackend.services;

import com.ygdrazil.pingo.pingobackend.models.BingoGrid;
import com.ygdrazil.pingo.pingobackend.models.User;
import com.ygdrazil.pingo.pingobackend.repositories.BingoRepository;
import com.ygdrazil.pingo.pingobackend.requestObjects.CreateBingoGridRequest;
import com.ygdrazil.pingo.pingobackend.responseObjects.BingoResponse;
import com.ygdrazil.pingo.pingobackend.utils.BingoGridNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BingoService {

    private final BingoRepository repository;

    public BingoResponse find(Long bingoId) {
        Optional<BingoGrid> potentialGrid = repository.findById(bingoId);

        if(potentialGrid.isEmpty())
            throw new BingoGridNotFoundException(bingoId);

        BingoGrid bingoGrid = potentialGrid.get();

        return BingoResponse.builder()
                .id(bingoGrid.getId())
                .name(bingoGrid.getName())
                .gridData(bingoGrid.getGridData())
                .build();
    }

    public List<BingoResponse> findAll() {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        System.out.println("Requested by : " + authenticatedUser.getId());

        List<BingoGrid> gridList = repository.findAll();

        return gridList.stream().map(bingoGrid -> new BingoResponse(bingoGrid.getId(), bingoGrid.getName(), bingoGrid.getGridData())).collect(Collectors.toList());
    }

    public BingoResponse insert(CreateBingoGridRequest request) {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        System.out.println("Requested by : " + authenticatedUser.getId());

        BingoGrid bingoGrid = BingoGrid.builder()
                .name(request.getName())
                .gridData(request.getGridData())
                .build();

        bingoGrid = repository.save(bingoGrid);

        return BingoResponse.builder()
                .id(bingoGrid.getId())
                .name(bingoGrid.getName())
                .gridData(bingoGrid.getGridData())
                .build();
    }

    public BingoResponse modify(Long bingoId, CreateBingoGridRequest body) {
        Optional<BingoGrid> gridToModify = repository.findById(bingoId);

        if(gridToModify.isEmpty())
            throw new BingoGridNotFoundException(bingoId);

        BingoGrid bingoGrid = gridToModify.get();

        bingoGrid.setName(body.getName());
        bingoGrid.setGridData(body.getGridData());

        bingoGrid = repository.save(bingoGrid);

        return BingoResponse.builder()
                .id(bingoGrid.getId())
                .name(bingoGrid.getName())
                .gridData(bingoGrid.getGridData())
                .build();
    }
}
