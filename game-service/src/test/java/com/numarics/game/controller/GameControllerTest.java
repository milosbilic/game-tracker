package com.numarics.game.controller;

import com.numarics.game.model.dto.GameDetailsDto;
import com.numarics.game.model.dto.StartGameRequestDto;
import com.numarics.game.model.dto.StartGameResponseDto;
import com.numarics.game.model.dto.UpdateGameStatusDto;
import com.numarics.game.model.entity.Game;
import com.numarics.game.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.List;

import static com.numarics.game.TestUtil.generateId;
import static com.numarics.game.model.entity.Game.Status.FINISHED;
import static com.numarics.game.model.entity.Game.Status.NEW;
import static net.bytebuddy.utility.RandomString.make;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    @Mock
    GameService gameService;

    @Mock
    ConversionService conversionService;

    GameController controller;

    @BeforeEach
    void setUp() {
        controller = new GameController(gameService, conversionService);
    }

    @Test
    @DisplayName("Get details - ok")
    void getDetails_ok() {
        // Given
        Long id = generateId();
        var game = mock(Game.class);
        var dto = mock(GameDetailsDto.class);

        // When
        when(gameService.getById(id)).thenReturn(game);
        when(conversionService.convert(game, GameDetailsDto.class)).thenReturn(dto);

        var result = controller.getDetails(id);

        // Then
        assertThat(result).isEqualTo(dto);
        verifyNoMoreInteractions(gameService, conversionService);
    }

    @Test
    @DisplayName("Update game status - ok")
    void updateGameStatus_ok() {
        // Given
        Long id = generateId();
        var game = mock(Game.class);
        var dto = new UpdateGameStatusDto(FINISHED);
        var gameDetailsDto = mock(GameDetailsDto.class);

        // When
        when(gameService.updateGameStatus(id, FINISHED)).thenReturn(game);
        when(conversionService.convert(game, GameDetailsDto.class)).thenReturn(gameDetailsDto);

        var result = controller.updateGameStatus(id, dto);

        // Then
        assertThat(result).isEqualTo(gameDetailsDto);
        verifyNoMoreInteractions(gameService, conversionService);
    }

    @Test
    @DisplayName("Delete game - ok")
    void deleteGame_ok() {
        // Given
        Long id = generateId();

        // When
        controller.deleteGame(id);

        // Then
        verify(gameService, times(1)).deleteGame(id);
        verifyNoMoreInteractions(gameService);
    }

    @Test
    @DisplayName("Search - ok")
    void search_ok() {
        // Given
        String name = make();
        String playerName = make();
        var game = mock(Game.class);
        var dto = mock(GameDetailsDto.class);

        // When
        when(gameService.search(any())).thenReturn(List.of(game));
        when(conversionService.convert(game, GameDetailsDto.class)).thenReturn(dto);

        var result = controller.search(NEW, name, playerName);

        // Then
        assertThat(result).isEqualTo(List.of(dto));
    }

    @Test
    @DisplayName("Start game - ok")
    void startGame_ok() {
        // Given
        var dto = mock(StartGameRequestDto.class);
        var responseDto = mock(StartGameResponseDto.class);
        var game = mock(Game.class);

        // When
        when(gameService.startGame(dto)).thenReturn(game);
        when(conversionService.convert(game, StartGameResponseDto.class)).thenReturn(responseDto);

        var result = controller.startGame(dto);

        // Then
        assertThat(result).isEqualTo(responseDto);
    }
}
