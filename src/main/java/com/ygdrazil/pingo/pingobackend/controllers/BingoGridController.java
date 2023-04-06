package com.ygdrazil.pingo.pingobackend.controllers;

import com.ygdrazil.pingo.pingobackend.auth.AuthenticationService;
import com.ygdrazil.pingo.pingobackend.models.BingoGrid;
import com.ygdrazil.pingo.pingobackend.models.User;
import com.ygdrazil.pingo.pingobackend.models.WSMessage;
import com.ygdrazil.pingo.pingobackend.requestObjects.CreateBingoGridRequest;
import com.ygdrazil.pingo.pingobackend.responseObjects.BingoResponse;
import com.ygdrazil.pingo.pingobackend.services.BingoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bingo")
@RequiredArgsConstructor
public class BingoGridController {

    private final BingoService bingoService;
    private final AuthenticationService authenticationService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{bingo_id}")
    public ResponseEntity<?> find(
            @PathVariable Long bingo_id
    ){
        Optional<BingoGrid> grid = bingoService.find(bingo_id);

        if(grid.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body("Error, resource not found");
        }

        return ResponseEntity.ok(new BingoResponse(grid.get()));
    }

    @GetMapping("/url_code/{bingo_code}")
    public ResponseEntity<?> findByCode(
            @PathVariable String bingo_code
    ){
        Optional<BingoGrid> grid = bingoService.findByUrlCode(bingo_code);

        if(grid.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body("Error, resource not found");
        }

        return ResponseEntity.ok(new BingoResponse(grid.get()));
    }

    @GetMapping
    public ResponseEntity<List<BingoResponse>> findAll() {
        List<BingoGrid> gridList = bingoService.findAll();

        List<BingoResponse> bingoResponseList = gridList.stream().map(bingoGrid -> new BingoResponse(
                bingoGrid.getId(),
                bingoGrid.getUser().getId(),
                bingoGrid.getUrlCode(),
                bingoGrid.getName(),
                bingoGrid.getDim(),
                bingoGrid.getGridData())).collect(Collectors.toList());

        return ResponseEntity.ok(bingoResponseList);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> insert(
            @RequestBody CreateBingoGridRequest body
    ) {
        Optional<User> potAuthUser = authenticationService.getAuthenticatedUser();

        if(potAuthUser.isEmpty()) {
            return ResponseEntity
                    .status(403)
                    .body("Error, you are not authenticated");
        }

        User authUser = potAuthUser.get();

        if(body.getDim() < 3 || body.getDim() % 2 == 0 || (body.getDim() * body.getDim()) > body.getGridData().size()) {
            return ResponseEntity
                    .status(400)
                    .body("Error, bad request");
        }

        Optional<BingoGrid> potCreatedGrid = bingoService.insert(body, authUser);

        if(potCreatedGrid.isEmpty()) {
            return ResponseEntity
                    .status(409)
                    .body("Error, resource name already exists");
        }

        return ResponseEntity.status(201).body(new BingoResponse(potCreatedGrid.get()));
    }

    @PutMapping("/{bingo_id}")
    @Transactional
    public ResponseEntity<?> modify(
            @RequestBody CreateBingoGridRequest body,
            @PathVariable Long bingo_id
    ) {
        Optional<User> potAuthUser = authenticationService.getAuthenticatedUser();

        if(potAuthUser.isEmpty()) {
            return ResponseEntity
                    .status(403)
                    .body("Error, you are not authenticated");
        }

        User authUser = potAuthUser.get();

        if(body.getDim() < 3 || body.getDim() % 2 == 0 || (body.getDim() * body.getDim()) > body.getGridData().size()) {
            return ResponseEntity
                    .status(400)
                    .body("Error, bad request");
        }

        Optional<BingoGrid> potGrid = bingoService.find(bingo_id);

        if(potGrid.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body("Error, resource not found");
        }

        BingoGrid grid = potGrid.get();

        if(!Objects.equals(grid.getUser().getId(), authUser.getId())) {
            return ResponseEntity
                    .status(401)
                    .body("Error, unauthorized access to resource");
        }

        grid = bingoService.modify(grid, body);

        return ResponseEntity.ok(new BingoResponse(grid));
    }

    @DeleteMapping("/{url_code}")
    @Transactional
    public ResponseEntity<?> delete(
            @PathVariable String url_code
    ) {
        Optional<User> potAuthUser = authenticationService.getAuthenticatedUser();

        if(potAuthUser.isEmpty()) {
            return ResponseEntity
                    .status(403)
                    .body("Error, you are not authenticated");
        }

        User authUser = potAuthUser.get();

        Optional<BingoGrid> potGrid = bingoService.findByUrlCode(url_code);

        if(potGrid.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body("Error, resource not found");
        }

        BingoGrid grid = potGrid.get();

        if(!Objects.equals(grid.getUser().getId(), authUser.getId())) {
            return ResponseEntity
                    .status(401)
                    .body("Error, unauthorized access to resource");
        }

        bingoService.delete(grid);

        return ResponseEntity.ok("Grid deleted");
    }


    // Websocket Logic

    @MessageMapping("/bingo_rooms/{roomUrl}")
    public void send(@DestinationVariable String roomUrl, WSMessage message) {
        String username = message.getFrom();
        messagingTemplate.convertAndSend("/topic/messages/" + roomUrl, message, createHeaders(username));
        messagingTemplate.convertAndSendToUser(username, "/queue/messages/" + roomUrl, message, createHeaders(username));
    }

    private MessageHeaders createHeaders(String username) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setHeader("sender", username);
        return headerAccessor.getMessageHeaders();
    }



}
