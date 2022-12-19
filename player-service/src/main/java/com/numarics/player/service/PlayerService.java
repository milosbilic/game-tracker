package com.numarics.player.service;

import com.numarics.player.model.dto.RegisterPlayerRequestDto;
import com.numarics.player.model.dto.UpdatePlayerGameRequestDto;
import com.numarics.player.model.entity.Player;

import java.util.List;

public interface PlayerService {

    Player registerPlayer(RegisterPlayerRequestDto dto);

    Player getById(Long id);

    void deletePlayer(Long id);

    List<Player> findByName(String name);

    Player updatePlayerGame(Long id, UpdatePlayerGameRequestDto dto);

    void removeGameForPlayers(Long gameId);
}
