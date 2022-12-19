package com.numarics.game.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.numarics.game.HttpBasedTest;
import com.numarics.game.model.entity.Game;
import com.numarics.game.repository.GameRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import java.util.Arrays;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.numarics.game.TestUtil.generateId;
import static com.numarics.game.model.entity.Game.Status.*;
import static io.restassured.RestAssured.given;
import static net.bytebuddy.utility.RandomString.make;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@DisplayName("Game controller")
@AutoConfigureWireMock(port = 8081)
public class GameControllerIT extends HttpBasedTest {

    @Autowired
    GameRepository gameRepository;

    @BeforeAll
    static void setUp() {
        startWireMockServer();
    }

    @AfterAll
    static void cleanUp() {
        stopWireMockServer();
    }

    @Test
    @DisplayName("Get details - ok")
    void getDetails_ok() {
        var game = createGame();

        given()
                .headers(defaultHeaders())
                .when()
                .get(url(GAME_URI), game.getId())
                .then()
                .statusCode(OK.value())
                .body("id", notNullValue())
                .body("name", equalTo(game.getName()))
                .body("status", equalTo(game.getStatus().toString()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    @DisplayName("Get details - player not found")
    void getDetails_playerNotFound() {
        given()
                .headers(defaultHeaders())
                .when()
                .get(url(GAME_URI), generateId())
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    @DisplayName("Update status - ok")
    void updateStatus_ok() throws JSONException {
        var game = createGame();

        given()
                .headers(defaultHeaders())
                .contentType(APPLICATION_JSON_VALUE)
                .body(updateStatusRequestJSON().toString())
                .when()
                .put(url(PLAY_URI), game.getId())
                .then()
                .statusCode(OK.value())
                .body("id", equalTo(game.getId().intValue()))
                .body("name", equalTo(game.getName()))
                .body("status", equalTo(FINISHED.toString()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    @DisplayName("Update status - not found")
    void updateStatus_notFound() throws JSONException {
        given()
                .headers(defaultHeaders())
                .contentType(APPLICATION_JSON_VALUE)
                .body(updateStatusRequestJSON().toString())
                .when()
                .put(url(PLAY_URI), generateId())
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    @DisplayName("Delete game - ok")
    void deleteGame_ok() {
        var game = createGame();
        stubForRemovingGame(game.getId());

        given()
                .headers(defaultHeaders())
                .when()
                .delete(url(GAME_URI), game.getId())
                .then()
                .statusCode(NO_CONTENT.value());
    }

    @Test
    @DisplayName("Delete game - not found")
    void deleteGame_notFound() {
        given()
                .headers(defaultHeaders())
                .when()
                .delete(url(GAME_URI), generateId())
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    @DisplayName("Search by name - ok, found matching game")
    void searchByName_okFoundMatchingGame() {
        String name1 = make();
        String name2 = make();
        var game = createGame(name1);
        createGame(name2);

        given()
                .headers(defaultHeaders())
                .when()
                .param("name", name1)
                .get(url(GAMES_URI))
                .then()
                .statusCode(OK.value())
                .body("", hasSize(1))
                .body("[0].id", equalTo(game.getId().intValue()))
                .body("[0].name", equalTo(game.getName()));
    }

    @Test
    @DisplayName("Search by status - ok, found matching game")
    void searchByStatus_okFoundMatchingGame() {
        gameRepository.deleteAll();
        var droppedGame1 = createGame(DROPPED);
        createGame(NEW);
        createGame(FINISHED);
        var droppedGame2 = createGame(DROPPED);

        given()
                .headers(defaultHeaders())
                .when()
                .param("status", DROPPED.toString())
                .get(url(GAMES_URI))
                .then()
                .statusCode(OK.value())
                .body("", hasSize(2))
                .body("id", contains(droppedGame1.getId().intValue(), droppedGame2.getId().intValue()))
                .body("status", contains(DROPPED.name(), DROPPED.name()));
    }

    @Test
    @DisplayName("Search by player name - ok, found matching game")
    void searchByPlayerName_okFoundMatchingGame() throws JSONException {
        createGame(DROPPED);
        createGame(NEW);
        var game = createGame(FINISHED);
        String playerName = make();

        stubForGameSearchByPlayerName(playerName, game.getId());

        given()
                .headers(defaultHeaders())
                .when()
                .param("playerName", playerName)
                .get(url(GAMES_URI))
                .then()
                .statusCode(OK.value())
                .body("", hasSize(1))
                .body("[0].id", equalTo(game.getId().intValue()))
                .body("[0].name", equalTo(game.getName()))
                .body("[0].status", equalTo(game.getStatus().name()));
    }

    @Test
    @DisplayName("Search by status and name - ok, found matching game")
    void searchByStatusAndName_okFoundMatchingGame() {
        var name = make();
        createGame(DROPPED);
        createGame(NEW);
        createGame(FINISHED);
        var droppedGame2 = createGame(name, DROPPED);

        given()
                .headers(defaultHeaders())
                .when()
                .param("status", DROPPED.toString())
                .param("name", name)
                .get(url(GAMES_URI))
                .then()
                .statusCode(OK.value())
                .body("", hasSize(1))
                .body("[0].id", equalTo(droppedGame2.getId().intValue()))
                .body("[0].name", equalTo(droppedGame2.getName()))
                .body("[0].status", equalTo(droppedGame2.getStatus().name()));
    }

    @Test
    @DisplayName("Start game - missing name")
    void startGame_missingName() throws JSONException {
        var startGame = startGameJSON()
                .put("name", null);

        given()
                .headers(defaultHeaders())
                .contentType(APPLICATION_JSON_VALUE)
                .body(startGame.toString())
                .when()
                .post(url(PLAY_GAME_URI))
                .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Start game - ok, as unregistered player")
    void startGame_okAsUnregisteredPlayer() throws JSONException {
        var startGame = startGameJSON()
                .put("playerId", null);

        stubForPlayerRegistration();

        given()
                .headers(defaultHeaders())
                .contentType(APPLICATION_JSON_VALUE)
                .body(startGame.toString())
                .when()
                .post(url(PLAY_GAME_URI))
                .then()
                .statusCode(CREATED.value())
                .body("id", notNullValue())
                .body("name", equalTo(startGame.getString("name")))
                .body("status", equalTo(NEW.name()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    @DisplayName("Start game - ok, as registered player")
    void startGame_okAsRegisteredPlayer() throws JSONException {
        var startGame = startGameJSON();

        stubForPlayerGameUpdate(startGame.getLong("playerId"));

        given()
                .headers(defaultHeaders())
                .contentType(APPLICATION_JSON_VALUE)
                .body(startGame.toString())
                .when()
                .post(url(PLAY_GAME_URI))
                .then()
                .statusCode(CREATED.value())
                .body("id", notNullValue())
                .body("name", equalTo(startGame.getString("name")))
                .body("status", equalTo(NEW.name()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    Game createGame() {
        return createGame(make());
    }

    Game createGame(String name) {
        return createGame(name, NEW);
    }

    Game createGame(Game.Status status) {
        return createGame(make(), status);
    }

    Game createGame(String name, Game.Status status) {
        return gameRepository.save(new Game()
                .setName(name)
                .setStatus(status));
    }

    JSONObject startGameJSON() throws JSONException {
        return new JSONObject()
                .put("name", make())
                .put("playerId", generateId());
    }

    JSONObject updateStatusRequestJSON() throws JSONException {
        return new JSONObject()
                .put("status", Game.Status.FINISHED);
    }

    void stubForRemovingGame(Long gameId) {
        stubFor(put(WireMock.urlEqualTo("/player/games/" + gameId)));
    }

    void stubForGameSearchByPlayerName(String playerName, Long gameId) throws JSONException {
        stubFor(WireMock.get(WireMock.urlEqualTo("/player/" + playerName + "/games"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody(gameSearchResponseJSON(gameId).toString())));
    }

    void stubForPlayerRegistration() {
        stubFor(WireMock.post(WireMock.urlEqualTo("/player/register"))
                .withHeader(ACCEPT, WireMock.containing(APPLICATION_JSON_VALUE))
                .withHeader(CONTENT_TYPE, WireMock.containing(APPLICATION_JSON_VALUE))
                .withRequestBody(WireMock.matchingJsonPath("$.name"))
                .withRequestBody(WireMock.matchingJsonPath("$.gameId"))
                .willReturn(aResponse().withStatus(CREATED.value())));
    }

    void stubForPlayerGameUpdate(Long playerId) {
        stubFor(WireMock.patch(WireMock.urlEqualTo("/player/" + playerId))
                .withHeader(ACCEPT, WireMock.containing(APPLICATION_JSON_VALUE))
                .withHeader(CONTENT_TYPE, WireMock.containing(APPLICATION_JSON_VALUE))
                .withRequestBody(WireMock.matchingJsonPath("$.gameId"))
                .willReturn(aResponse().withStatus(OK.value())));
    }

    JSONObject gameSearchResponseJSON(Long... ids) throws JSONException {
        var json = new JSONObject();
        var games = new JSONArray();
        Arrays.stream(ids)
                .forEach(games::put);
        json.put("games", games);

        return json;
    }

    JSONObject registerPlayerJSON(Long gameId) throws JSONException {
        return new JSONObject()
                .put("name", UUID.randomUUID().toString())
                .put("gameId", gameId);
    }
}
