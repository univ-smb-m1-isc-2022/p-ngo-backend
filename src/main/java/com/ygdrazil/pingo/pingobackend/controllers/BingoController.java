package com.ygdrazil.pingo.pingobackend.controllers;

import com.ygdrazil.pingo.pingobackend.requestObjects.CreateBingoGridRequest;
import com.ygdrazil.pingo.pingobackend.responseObjects.BingoResponse;
import com.ygdrazil.pingo.pingobackend.services.BingoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bingo")
@RequiredArgsConstructor
public class BingoController {
    private final BingoService bingoService;

    @GetMapping("/{bingo_id}")
    public ResponseEntity<BingoResponse> find(
            @PathVariable Long bingo_id
    ){
        return ResponseEntity.ok(bingoService.find(bingo_id));
    }

    @GetMapping
    public ResponseEntity<List<BingoResponse>> findAll(){
        return ResponseEntity.ok(bingoService.findAll());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<BingoResponse> insert(
            @RequestBody CreateBingoGridRequest body
    ) {
        return ResponseEntity.ok(bingoService.insert(body));
    }

    @PutMapping("/{bingo_id}")
    @Transactional
    public ResponseEntity<BingoResponse> modify(
            @RequestBody CreateBingoGridRequest body,
            @PathVariable Long bingo_id
    ) {
        return ResponseEntity.ok(bingoService.modify(bingo_id,body));
    }

}
