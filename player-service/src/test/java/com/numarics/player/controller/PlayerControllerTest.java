package com.numarics.player.controller;

import com.numarics.player.model.dto.PlayerDetailsDto;
import com.numarics.player.model.dto.RegisterPlayerRequestDto;
import com.numarics.player.model.dto.RegisterPlayerResponseDto;
import com.numarics.player.model.dto.UpdatePlayerGameRequestDto;
import com.numarics.player.model.entity.Player;
import com.numarics.player.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.List;

import static com.numarics.player.TestUtil.generateId;
import static net.bytebuddy.utility.RandomString.make;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerControllerTest {

    PlayerController controller;

    @Mock
    PlayerService playerService;

    @Mock
    ConversionService conversionService;

    @Mock
    Player player;

    @BeforeEach
    void setUp() {
        controller = new PlayerController(playerService, conversionService);
    }

    @Test
    @DisplayName("Register player - ok")
    void registerPlayer_ok() {
        // Given
        var dto = mock(RegisterPlayerRequestDto.class);
        var responseDto = mock(RegisterPlayerResponseDto.class);

        // When
        when(playerService.registerPlayer(dto)).thenReturn(player);
        when(conversionService.convert(player, RegisterPlayerResponseDto.class)).thenReturn(responseDto);

        var result = controller.registerPlayer(dto);

        // Then
        assertThat(result).isEqualTo(responseDto);
        verifyNoMoreInteractions(playerService, conversionService);
    }

    @Test
    @DisplayName("Get player details - ok")
    void getPlayerDetails_ok() {
        // Given
        Long id = generateId();
        var dto = mock(PlayerDetailsDto.class);

        // When
        when(playerService.getById(id)).thenReturn(player);
        when(conversionService.convert(player, PlayerDetailsDto.class)).thenReturn(dto);

        var result = controller.getPlayerDetails(id);

        //Then
        assertThat(result).isEqualTo(dto);
        verifyNoMoreInteractions(playerService, conversionService);
    }

    @Test
    @DisplayName("Delete player - ok")
    void deletePlayer_ok() {
        // Given
        Long id = generateId();

        // When
        controller.deletePlayer(id);

        //Then
        verify(playerService, times(1)).deletePlayer(id);
        verifyNoMoreInteractions(playerService, conversionService);
    }

    @Test
    @DisplayName("Get game IDs - ok")
    void getGameIds_ok() {
        // Given
        String name = make();
        Long gameId = generateId();

        // When
        when(playerService.findByName(name)).thenReturn(List.of(player));
        when(player.getGameId()).thenReturn(gameId);

        var result = controller.getGameIds(name);

        // Then
        assertThat(result.games()).isEqualTo(List.of(gameId));
        verifyNoMoreInteractions(playerService, player);
    }

    @Test
    @DisplayName("Update player game - ok")
    void updatePlayerGame_ok() {
        // Given
        Long id = generateId();
        var dto = mock(UpdatePlayerGameRequestDto.class);
        var playerDetailsDto = mock(PlayerDetailsDto.class);

        // When
        when(playerService.updatePlayerGame(id, dto)).thenReturn(player);
        when(conversionService.convert(player, PlayerDetailsDto.class)).thenReturn(playerDetailsDto);

        var result = controller.updatePlayerGame(id, dto);

        //Then
        assertThat(result).isEqualTo(playerDetailsDto);
        verifyNoMoreInteractions(playerService, conversionService);
    }

    @Test
    @DisplayName("Remove game for players - ok")
    void removeGameForPlayers_ok() {
        // Given
        Long gameId = generateId();

        // When
        controller.removeGameForPlayers(gameId);

        // Then
        verify(playerService, times(1)).removeGameForPlayers(gameId);
        verifyNoMoreInteractions(playerService);
    }
}