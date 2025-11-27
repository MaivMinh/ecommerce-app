package com.minh.event_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.event_service.payload.response.GameResponse;
import com.minh.event_service.entity.Game;
import com.minh.event_service.payload.request.CreateGameRequest;
import com.minh.event_service.payload.request.UpdateGameRequest;
import com.minh.event_service.repository.GameRepository;
import com.minh.event_service.service.GameService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;
    private final MessageCommon messageCommon;

    @Override
    public void createGame(CreateGameRequest request) {
        Game game = new Game();
        game.setId(AppUtils.generateUUIDv7());
        game.setName(request.getName());

        gameRepository.save(game);
    }

    @Override
    public ResponseData getAllGames() {
        List<Game> games = gameRepository.findAll();
        List<GameResponse> gameDTOs = games.stream()
                .map(game -> {
                    return modelMapper.map(game, GameResponse.class);
                })
                .toList();
        return ResponseData.builder()
                .status(200)
                .message("Success")
                .data(gameDTOs)
                .build();
    }

    @Override
    public void updateGame(UpdateGameRequest request) {
        Game saved = gameRepository.findById(request.getId()).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Game.NOT_FOUND, request.getId()))
        );

        saved.setName(request.getName());
        gameRepository.save(saved);
    }

    @Override
    public void deleteGame(String id) {
        Game saved = gameRepository.findById(id).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Game.NOT_FOUND, id))
        );
        gameRepository.deleteById(saved.getId());
    }


    @Override
    public GameResponse getGameDetailById(String gameId) {
        if (!StringUtils.hasText(gameId)) {
            return null;
        }
        Game game = gameRepository.findById(gameId).orElse(null);
        if (Objects.isNull(game)) {
            return null;
        }
        return modelMapper.map(game, GameResponse.class);
    }
}