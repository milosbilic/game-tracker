package com.numarics.game.client;

import com.numarics.game.client.dto.RegisterPlayerRequestDto;
import com.numarics.game.client.dto.UpdatePlayerGameRequestDto;
import com.numarics.game.configuration.AppProperties;
import com.numarics.game.model.dto.GameSearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

import static java.util.UUID.randomUUID;
import static org.apache.hc.core5.http.HttpVersion.HTTP;

@Component
@RequiredArgsConstructor
public class PlayerClient {

    private final AppProperties appProperties;
    private final RestTemplate restTemplate;
    private static final String GAMES = "games";

    public void registerPlayer(Long gameId) {
        String requestUrl = buildRequestUri("register");
        var dto = new RegisterPlayerRequestDto(randomUUID().toString(), gameId);
        restTemplate.postForObject(requestUrl, dto, Object.class);
    }

    public void updatePlayerGame(Long playerId, Long gameId) {
        String requestUrl = buildRequestUri(playerId.toString());
        var dto = new UpdatePlayerGameRequestDto(gameId);
        restTemplate.patchForObject(requestUrl, dto, Object.class);
    }

    public GameSearchResponseDto getGamesByPlayerName(String playerName) {
        String requestUrl = buildRequestUri(playerName, GAMES);

        return restTemplate.getForObject(requestUrl, GameSearchResponseDto.class);
    }

    public void removeGame(Long gameId) {
        String requestUrl = buildRequestUri(GAMES, gameId.toString());

        restTemplate.put(requestUrl, null);
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
