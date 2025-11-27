package com.minh.event_service.controller;

import com.minh.common.constants.ResponseMessages;
import com.minh.common.response.ResponseData;
import com.minh.event_service.payload.request.CreateGameRequest;
import com.minh.event_service.payload.request.UpdateGameRequest;
import com.minh.event_service.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/games")
@RestController
@Validated
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> createGame(@RequestBody @Valid CreateGameRequest request) {
        gameService.createGame(request);
        return ResponseEntity.ok(ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build());
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> getAllGames() {
        ResponseData response = gameService.getAllGames();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> updateGame(@RequestBody @Valid UpdateGameRequest request) {
        gameService.updateGame(request);
        return ResponseEntity.ok(ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build());
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> deleteGame(@PathVariable("id") String id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok(ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build());
    }
}