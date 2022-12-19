package com.numarics.game.service;

import com.numarics.game.client.PlayerClient;
import com.numarics.game.exception.ResourceNotFoundException;
import com.numarics.game.model.dto.GameSearchResponseDto;
import com.numarics.game.model.dto.StartGameRequestDto;
import com.numarics.game.model.entity.Game;
import com.numarics.game.model.filter.GameSearchFilter;
import com.numarics.game.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.numarics.game.TestUtil.generateId;
import static com.numarics.game.model.entity.Game.Status.DROPPED;
import static com.numarics.game.model.entity.Game.Status.NEW;
import static java.util.Collections.emptyList;
import static net.bytebuddy.utility.RandomString.make;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    @Mock
    GameRepository gameRepository;

    @Mock
    PlayerClient playerClient;

    GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameServiceImpl(gameRepository, playerClient);
    }

    @Test
    @DisplayName("Get by ID - ok, found")
    void getById_okFound() {
        // Given
        Long id = generateId();
        var game = mock(Game.class);

        // When
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));

        var result = gameService.getById(id);

        // Then
        assertThat(result).isEqualTo(game);
    }

    @Test
    @DisplayName("Get by ID - not found")
    void getById_notFound() {
        // Given
        Long id = generateId();

        // When
        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        assertThrows(ResourceNotFoundException.class, () -> gameService.getById(id));
    }

    @Test
    @DisplayName("Update game status - ok, game status updated")
    void updateGameStatus_okGameStatusUpdated() {
        // Given
        Long id = generateId();
        var game = new Game()
                .setName(make())
                .setStatus(NEW);

        // When
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        when(gameRepository.save(any(Game.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        var result = gameService.updateGameStatus(id, DROPPED);

        // Then
        assertThat(result).isEqualTo(game);
    }

    @Test
    @DisplayName("Update game status - not found")
    void updateGameStatus_notFound() {
        // Given
        Long id = generateId();

        // When
        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        assertThrows(ResourceNotFoundException.class, () -> gameService.updateGameStatus(id, DROPPED));
    }

    @Test
    @DisplayName("Delete game - ok")
    void deleteGame_ok() {
        // Given
        Long id = generateId();
        var game = mock(Game.class);

        // When
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));

        gameService.deleteGame(id);

        // Then
        verify(gameRepository, times(1)).delete(game);
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("Search - ok, found by player name")
    void search_okFoundByPlayerName() {
        // Given
        String playerName = make();
        var filter = new GameSearchFilter()
                .setPlayerName(playerName);
        Long gameId = generateId();
        var game = mock(Game.class);

        // When
        when(playerClient.getGamesByPlayerName(playerName)).thenReturn(new GameSearchResponseDto(List.of(gameId)));
        when(gameRepository.search(filter)).thenReturn(List.of(game));
        var result = gameService.search(filter);


        // Then
        assertThat(result).isEqualTo(List.of(game));
        verifyNoMoreInteractions(playerClient, gameRepository);
    }

    @Test
    @DisplayName("Search - ok, not found by name")
    void search_okNotFoundByName() {
        // Given
        String playerName = make();
        var filter = new GameSearchFilter()
                .setPlayerName(playerName);

        // When
        when(playerClient.getGamesByPlayerName(playerName)).thenReturn(new GameSearchResponseDto(emptyList()));
        when(gameRepository.search(filter)).thenReturn(emptyList());
        var result = gameService.search(filter);

        // Then
        assertThat(result).isEmpty();
        verifyNoMoreInteractions(playerClient, gameRepository);
    }

    @Test
    @DisplayName("Start game - ok, player already registered")
    void startGame_okPlayerAlreadyRegistered() {
        // Given
        var dto = mock(StartGameRequestDto.class);
        Long playerId = generateId();
        var game = mock(Game.class);
        Long gameId = generateId();

        // When
        when(dto.playerId()).thenReturn(playerId);
        when(gameRepository.save(any())).thenReturn(game);
        when(game.getId()).thenReturn(gameId);
        gameService.startGame(dto);

        // Then
        verify(playerClient, times(1)).updatePlayerGame(playerId, gameId);
        verifyNoMoreInteractions(gameRepository, playerClient);
    }

    @Test
    @DisplayName("Start game - ok, player not registered")
    void startGame_okPlayerNotRegistered() {
        // Given
        var dto = mock(StartGameRequestDto.class);
        var game = mock(Game.class);
        Long gameId = generateId();

        // When
        when(dto.playerId()).thenReturn(null);
        when(gameRepository.save(any())).thenReturn(game);
        when(game.getId()).thenReturn(gameId);
        gameService.startGame(dto);

        // Then
        verify(playerClient, times(1)).registerPlayer(gameId);
        verifyNoMoreInteractions(gameRepository, playerClient);
    }
}