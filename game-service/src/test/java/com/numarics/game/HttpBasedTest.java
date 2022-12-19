package com.numarics.game;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.numarics.game.configuration.AppProperties;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static java.lang.Integer.parseInt;
import static java.util.Collections.singletonList;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpBasedTest {

    @LocalServerPort
    int port;

    protected static final String GAMES_URI = "/game";
    protected static final String GAME_URI = GAMES_URI + "/{id}";
    protected static final String PLAY_URI = GAME_URI + "/play";
    protected static final String PLAY_GAME_URI = GAMES_URI + "/play";

    protected static WireMockServer wireMockServer;

    @Autowired
    AppProperties appProperties;


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

    protected static void startWireMockServer() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        configureFor("localhost", 8081);
    }

    protected static void stopWireMockServer() {
        wireMockServer.stop();
    }
}
