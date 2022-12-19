package com.numarics.player.service.impl;

import com.numarics.player.exception.ResourceNotFoundException;
import com.numarics.player.model.dto.RegisterPlayerRequestDto;
import com.numarics.player.model.dto.UpdatePlayerGameRequestDto;
import com.numarics.player.model.entity.Player;
import com.numarics.player.repository.PlayerRepository;
import com.numarics.player.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.numarics.player.TestUtil.generateId;
import static net.bytebuddy.utility.RandomString.make;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceImplTest {

    PlayerService playerService;

    @Mock
    PlayerRepository playerRepository;

    @Mock
    Player player;

    @BeforeEach
    void setUp() {
        playerService = new PlayerServiceImpl(playerRepository);
    }

    @Test
    @DisplayName("Register player - ok")
    void registerPlayer_ok() {
        // Given
        var dto = mock(RegisterPlayerRequestDto.class);

        // When
        when(playerRepository.save(any())).thenReturn(player);
        var result = playerService.registerPlayer(dto);

        // Then
        assertThat(result).isEqualTo(player);
        verifyNoMoreInteractions(playerRepository);
    }

    @Test
    @DisplayName("Get by ID - ok, found")
    void getById_okFound() {
        // Given
        Long id = generateId();

        // When
        when(playerRepository.findById(id)).thenReturn(Optional.of(player));
        var result = playerService.getById(id);

        // Then
        assertThat(result).isEqualTo(player);
        verifyNoMoreInteractions(playerRepository);
    }

    @Test
    @DisplayName("Get by ID - not found")
    void getById_notFound() {
        // Given
        Long id = generateId();

        // When
        when(playerRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        assertThrows(ResourceNotFoundException.class, () -> playerService.getById(id));
    }

    @Test
    @DisplayName("Delete player - ok")
    void deletePlayer_ok() {
        // Given
        Long id = generateId();

        // When
        when(playerRepository.findById(id)).thenReturn(Optional.of(player));
        playerService.deletePlayer(id);

        // Then
        verify(playerRepository, times(1)).delete(player);
        verifyNoMoreInteractions(playerRepository);
    }

    @Test
    @DisplayName("Delete player - not found")
    void deletePlayer_notFound() {
        // Given
        Long id = generateId();

        // When
        when(playerRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        assertThrows(ResourceNotFoundException.class, () -> playerService.deletePlayer(id));
    }

    @Test
    @DisplayName("Find by name - ok")
    void findByName_ok() {
        // Given
        String name = make();

        // When
        when(playerRepository.findByName(name)).thenReturn(List.of(player));
        var result = playerService.findByName(name);

        // Then
        assertThat(result).isEqualTo(List.of(player));
        verifyNoMoreInteractions(playerRepository);
    }

    @Test
    @DisplayName("Update player game - ok")
    void updatePlayerGame_ok() {
        // Given
        Long id = generateId();
        var dto = mock(UpdatePlayerGameRequestDto.class);

        // When
        when(playerRepository.findById(id)).thenReturn(Optional.of(player));
        when(player.setGameId(dto.gameId())).thenReturn(player);

        var result = playerService.updatePlayerGame(id, dto);

        // Then
        assertThat(result).isEqualTo(player);
    }

    @Test
    @DisplayName("Update player game - user not found")
    void updatePlayerGame_userNotFound() {
        // Given
        Long id = generateId();
        var dto = mock(UpdatePlayerGameRequestDto.class);

        // When
        when(playerRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        assertThrows(ResourceNotFoundException.class, () -> playerService.updatePlayerGame(id, dto));
    }

    @Test
    @DisplayName("Remove game for player - ok")
    void removeGameForPlayers_ok() {
        // Given
        Long id = generateId();

        // When
        when(playerRepository.findByGameId(id)).thenReturn(List.of(player));
        when(playerRepository.saveAll(anyList())).thenReturn(List.of(player));

        playerService.removeGameForPlayers(id);

        // Then
        verifyNoMoreInteractions(playerRepository);
    }
}