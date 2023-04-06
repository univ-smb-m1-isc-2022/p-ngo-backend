package com.ygdrazil.pingo.pingobackend.controllers;

import com.ygdrazil.pingo.pingobackend.auth.AuthenticationService;
import com.ygdrazil.pingo.pingobackend.models.BingoGrid;
import com.ygdrazil.pingo.pingobackend.models.BingoSave;
import com.ygdrazil.pingo.pingobackend.models.User;
import com.ygdrazil.pingo.pingobackend.requestObjects.CreateBingoSaveRequest;
import com.ygdrazil.pingo.pingobackend.responseObjects.BingoResponse;
import com.ygdrazil.pingo.pingobackend.services.BingoSaveService;
import com.ygdrazil.pingo.pingobackend.services.BingoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final BingoSaveService bingoSaveService;
    private final AuthenticationService authenticationService;
    private final BingoService bingoService;

    @GetMapping("/{user_id}/save/{url_code}")
    public ResponseEntity<?> findGridSaveByUserIdAndUrlCode(
            @PathVariable Long user_id,
            @PathVariable String url_code
    ) {
        Optional<User> potAuthUser = authenticationService.getAuthenticatedUser();

        Optional<ResponseEntity<?>> potErrorResponse = checkUser(potAuthUser, user_id);

        if(potErrorResponse.isPresent()){
            return potErrorResponse.get();
        }

        Optional<BingoSave> potBingoSave = bingoSaveService.findByUserAndUrlCode(user_id, url_code);

        if(potBingoSave.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body("Error, no save found for this grid");
        }

        return ResponseEntity.ok(potBingoSave.get());
    }

    @PostMapping("/{user_id}/save")
    public ResponseEntity<?> insertGridSave(
            @PathVariable Long user_id,
            @RequestBody CreateBingoSaveRequest body
            ) {
        Optional<User> potAuthUser = authenticationService.getAuthenticatedUser();

        Optional<ResponseEntity<?>> potErrorResponse = checkUser(potAuthUser, user_id);

        if(potErrorResponse.isPresent())
            return potErrorResponse.get();

        Optional<BingoSave> potBingoSave = bingoSaveService.insert(body, potAuthUser.get());

        if(potBingoSave.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body("Error, couldn't insert save for this grid");
        }

        return ResponseEntity.ok(potBingoSave.get());
    }

    @PutMapping("/{user_id}/save/{url_code}")
    public ResponseEntity<?> modifyGridSave(
            @PathVariable Long user_id,
            @PathVariable String url_code,
            @RequestBody CreateBingoSaveRequest body
    ) {
        Optional<User> potAuthUser = authenticationService.getAuthenticatedUser();

        Optional<ResponseEntity<?>> potErrorResponse = checkUser(potAuthUser, user_id);

        if(potErrorResponse.isPresent())
            return potErrorResponse.get();



        Optional<BingoSave> potBingoSave = bingoSaveService.modify(body, potAuthUser.get());

        if(potBingoSave.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body("Error, no save found for this grid");
        }

        return ResponseEntity.ok(potBingoSave.get());
    }

    @GetMapping("/{user_id}/bingo")
    public ResponseEntity<?> findAllGrids(
            @PathVariable Long user_id
    ) {
        Optional<User> potAuthUser = authenticationService.getAuthenticatedUser();

        if(potAuthUser.isEmpty()) {
            return ResponseEntity
                    .status(403)
                    .body("Error, you are not authenticated");
        }

        User authUser = potAuthUser.get();

        if(!Objects.equals(user_id, authUser.getId())) {
            return ResponseEntity
                    .status(401)
                    .body("Error, you are trying to access grids that are not your own");
        }

        List<BingoGrid> gridList = bingoService.findAllByUser(authUser);

        List<BingoResponse> bingoResponseList = gridList.stream().map(bingoGrid -> new BingoResponse(
                bingoGrid.getId(),
                bingoGrid.getUser().getId(),
                bingoGrid.getUrlCode(),
                bingoGrid.getName(),
                bingoGrid.getDim(),
                bingoGrid.getGridData())).toList();

        return ResponseEntity.ok(bingoResponseList);
    }

    private Optional<ResponseEntity<?>> checkUser(Optional<User> potAuthUser, Long userId) {

        if(potAuthUser.isEmpty()) {
            return Optional.of(ResponseEntity
                    .status(403)
                    .body("Error, you are not authenticated"));
        }

        User authUser = potAuthUser.get();

        if(!(Objects.equals(userId, authUser.getId()))) {
            return Optional.of(ResponseEntity
                    .status(401)
                    .body("Error, you are trying to access the save of someone else"));
        }

        return Optional.empty();
    }
}
