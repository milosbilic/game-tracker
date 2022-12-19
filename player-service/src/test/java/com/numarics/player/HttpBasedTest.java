package com.numarics.player;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.util.Collections.singletonList;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpBasedTest {

    @LocalServerPort
    int port;

    protected static final String PLAYERS_URI = "/player";
    protected static final String PLAYER_URI = PLAYERS_URI + "/{id}";
    protected static final String PLAYERS_GAMES_URI = PLAYERS_URI + "/{name}/games";
    protected static final String GAME_URI = PLAYERS_URI + "/games/{id}";
    protected static final String REGISTER_PLAYER_URI = PLAYERS_URI + "/register";

    static {
        RestAssuredConfig.config().getLogConfig()
                .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }

    protected String url(String path) {
        String URL_TEMPLATE = "http://localhost:%s%s";

        return String.format(URL_TEMPLATE, port, path);
    }

    protected HttpHeaders defaultHeaders() {
        var headers = new HttpHeaders();
        headers.setAccept(singletonList(MediaType.APPLICATION_JSON));

        return headers;
    }
}
