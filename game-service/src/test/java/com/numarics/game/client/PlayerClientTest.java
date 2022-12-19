package com.numarics.game.client;

import com.numarics.game.configuration.AppProperties;
import com.numarics.game.model.dto.GameSearchResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

import static com.numarics.game.TestUtil.generateId;
import static net.bytebuddy.utility.RandomString.make;
import static org.apache.hc.core5.http.HttpVersion.HTTP;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerClientTest {

    PlayerClient client;

    @Mock
    AppProperties appProperties;

    @Mock
    RestTemplate restTemplate;

    private static final String HOST = make();
    private static final String PORT = generateId().toString();
    private static final String ENTRY_POINT = make();

    @BeforeEach
    void setUp() {
        client = new PlayerClient(appProperties, restTemplate);
        when(appProperties.getPlayerServiceHost()).thenReturn(HOST);
        when(appProperties.getPlayerServicePort()).thenReturn(PORT);
        when(appProperties.getPlayerServiceEntryPoint()).thenReturn(ENTRY_POINT);
    }

    @Test
    @DisplayName("Register player - ok")
    void registerPlayer_ok() {
        // Given
        Long gameId = generateId();
        String requestUri = buildRequestUri("register");

        // When
        client.registerPlayer(gameId);

        // Then
        verify(restTemplate, times(1)).postForObject(eq(requestUri), any(), eq(Object.class));
    }

    @Test
    @DisplayName("Update player game - ok")
    void updatePlayerGame_ok() {
        // Given
        Long playerId = generateId();
        Long gameId = generateId();
        String requestUrl = buildRequestUri(playerId.toString());

        // When
        client.updatePlayerGame(playerId, gameId);

        // Then
        verify(restTemplate, times(1)).patchForObject(eq(requestUrl), any(), eq(Object.class));
    }

    @Test
    @DisplayName("Get games by player name - ok")
    void getGamesByPlayerName_ok() {
        // Given
        String name = make();
        String requestUrl = buildRequestUri(name, "games");

        // When
        client.getGamesByPlayerName(name);

        // Then
        verify(restTemplate, times(1)).getForObject(requestUrl, GameSearchResponseDto.class);
    }

    @Test
    @DisplayName("Remove game - ok")
    void removeGame() {
        // Given
        Long gameId = generateId();
        String requestUrl = buildRequestUri("games", gameId.toString());

        // When
        client.removeGame(gameId);

        // Then
        verify(restTemplate, times(1)).put(requestUrl, null);
    }

    private String buildRequestUri(String... paths) {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme(HTTP)
                .host(appProperties.getPlayerServiceHost())
                .port(appProperties.getPlayerServicePort())
                .pathSegment(appProperties.getPlayerServiceEntryPoint());
        Arrays.stream(paths)
                .forEach(builder::pathSegment);

        return builder.build().toString();
    }
}
