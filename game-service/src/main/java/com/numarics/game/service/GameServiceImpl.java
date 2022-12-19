package com.numarics.game.service;

import com.numarics.game.client.PlayerClient;
import com.numarics.game.exception.ResourceNotFoundException;
import com.numarics.game.model.dto.GameSearchResponseDto;
import com.numarics.game.model.dto.StartGameRequestDto;
import com.numarics.game.model.entity.Game;
import com.numarics.game.model.filter.GameSearchFilter;
import com.numarics.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final PlayerClient playerClient;

    @Override
    public Game getById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Game updateGameStatus(Long id, Game.Status status) {
        return gameRepository.findById(id)
                .map(game -> game.setStatus(status))
                .map(gameRepository::save)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public void deleteGame(Long id) {
        var game = gameRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        gameRepository.delete(game);

        playerClient.removeGame(id);
    }

    @Override
    public List<Game> search(GameSearchFilter filter) {
        filter.setGameIds(getGameIdsByPlayerName(filter.getPlayerName()));

        return gameRepository.search(filter);
    }

    @Override
    @Transactional
    public Game startGame(StartGameRequestDto dto) {
        var newGame = new Game()
                .setName(dto.name())
                .setStatus(Game.Status.NEW);

        var game = gameRepository.save(newGame);

        ofNullable(dto.playerId())
                .ifPresentOrElse(playerId -> playerClient.updatePlayerGame(playerId, game.getId()),
                        () -> playerClient.registerPlayer(game.getId()));
        return game;
    }

    private List<Long> getGameIdsByPlayerName(String playerName) {
        return ofNullable(playerName)
                .map(playerClient::getGamesByPlayerName)
                .map(GameSearchResponseDto::games)
                .orElse(new ArrayList<>());
    }
}
