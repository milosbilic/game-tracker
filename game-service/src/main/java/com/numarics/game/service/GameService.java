package com.numarics.game.service;

import com.numarics.game.model.dto.StartGameRequestDto;
import com.numarics.game.model.entity.Game;
import com.numarics.game.model.filter.GameSearchFilter;

import java.util.List;

public interface GameService {

    Game getById(Long id);

    Game updateGameStatus(Long id, Game.Status status);

    void deleteGame(Long id);

    List<Game> search(GameSearchFilter filter);

    Game startGame(StartGameRequestDto dto);
}
