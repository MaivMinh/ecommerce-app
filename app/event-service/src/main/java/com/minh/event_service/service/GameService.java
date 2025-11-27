package com.minh.event_service.service;

import com.minh.common.response.ResponseData;
import com.minh.event_service.payload.request.CreateGameRequest;
import com.minh.event_service.payload.request.UpdateGameRequest;
import com.minh.event_service.payload.response.GameResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public interface GameService {
    void createGame(@Valid CreateGameRequest request);

    ResponseData getAllGames();

    void updateGame(UpdateGameRequest request);

    void deleteGame(String id);

    GameResponse getGameDetailById(@NotBlank String gameId);
}
