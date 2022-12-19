package com.numarics.game.controller;

import com.numarics.game.model.dto.GameDetailsDto;
import com.numarics.game.model.dto.StartGameRequestDto;
import com.numarics.game.model.dto.StartGameResponseDto;
import com.numarics.game.model.dto.UpdateGameStatusDto;
import com.numarics.game.model.entity.Game;
import com.numarics.game.model.filter.GameSearchFilter;
import com.numarics.game.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final ConversionService conversionService;

    @PostMapping("play")
    @ResponseStatus(HttpStatus.CREATED)
    public StartGameResponseDto startGame(@Valid @RequestBody StartGameRequestDto dto) {
        var game = gameService.startGame(dto);

        return conversionService.convert(game, StartGameResponseDto.class);
    }

    @GetMapping("{id}")
    public GameDetailsDto getDetails(@PathVariable Long id) {
        var game = gameService.getById(id);

        return conversionService.convert(game, GameDetailsDto.class);
    }

    @PutMapping("{id}/play")
    public GameDetailsDto updateGameStatus(@PathVariable Long id,
                                           @Valid @RequestBody UpdateGameStatusDto status) {
        var game = gameService.updateGameStatus(id, status.status());

        return conversionService.convert(game, GameDetailsDto.class);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
    }

    @GetMapping
    public List<GameDetailsDto> search(@RequestParam(required = false) Game.Status status,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String playerName) {
        var filter = new GameSearchFilter()
                .setStatus(status)
                .setName(name)
                .setPlayerName(playerName);

        return gameService.search(filter).stream()
                .map(game -> conversionService.convert(game, GameDetailsDto.class))
                .toList();
    }

}
