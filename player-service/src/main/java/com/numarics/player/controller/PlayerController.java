package com.numarics.player.controller;

import com.numarics.player.model.dto.*;
import com.numarics.player.model.entity.Player;
import com.numarics.player.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    private final ConversionService conversionService;

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterPlayerResponseDto registerPlayer(@Valid @RequestBody RegisterPlayerRequestDto dto) {
        var player = playerService.registerPlayer(dto);

        return conversionService.convert(player, RegisterPlayerResponseDto.class);
    }

    @GetMapping("{id}")
    public PlayerDetailsDto getPlayerDetails(@PathVariable Long id) {
        var player = playerService.getById(id);

        return conversionService.convert(player, PlayerDetailsDto.class);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
    }

    @PatchMapping("{id}")
    public PlayerDetailsDto updatePlayerGame(@PathVariable Long id,
                                             @Valid @RequestBody UpdatePlayerGameRequestDto dto) {
        var player = playerService.updatePlayerGame(id, dto);

        return conversionService.convert(player, PlayerDetailsDto.class);
    }

    @GetMapping("{name}/games")
    public GameSearchResponseDto getGameIds(@PathVariable String name) {
        var games = playerService.findByName(name).stream()
                .map(Player::getGameId)
                .toList();

        return new GameSearchResponseDto(games);
    }

    @PutMapping("games/{id}")
    public void removeGameForPlayers(@PathVariable("id") Long gameId) {
        playerService.removeGameForPlayers(gameId);
    }
}
