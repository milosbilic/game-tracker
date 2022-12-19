package com.numarics.player.service.impl;

import com.numarics.player.exception.ResourceNotFoundException;
import com.numarics.player.model.dto.RegisterPlayerRequestDto;
import com.numarics.player.model.dto.UpdatePlayerGameRequestDto;
import com.numarics.player.model.entity.Player;
import com.numarics.player.repository.PlayerRepository;
import com.numarics.player.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Override
    @Transactional
    public Player registerPlayer(RegisterPlayerRequestDto dto) {
        var player = new Player()
                .setName(dto.name())
                .setGameId(dto.gameId());

        return playerRepository.save(player);
    }

    @Override
    public Player getById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    @Transactional
    public void deletePlayer(Long id) {
        var player = playerRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        playerRepository.delete(player);
    }

    @Override
    public List<Player> findByName(String name) {
        return playerRepository.findByName(name);
    }

    @Override
    @Transactional
    public Player updatePlayerGame(Long id, UpdatePlayerGameRequestDto dto) {
        return playerRepository.findById(id)
                .map(player -> player.setGameId(dto.gameId()))
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    @Transactional
    public void removeGameForPlayers(Long gameId) {
        var players = playerRepository.findByGameId(gameId).stream()
                .map(player -> player.setGameId(null))
                .toList();

        playerRepository.saveAll(players);
    }
}
