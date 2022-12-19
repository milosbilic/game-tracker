package com.numarics.player.controller;

import com.numarics.player.HttpBasedTest;
import com.numarics.player.model.entity.Player;
import com.numarics.player.repository.PlayerRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.numarics.player.TestUtil.generateId;
import static io.restassured.RestAssured.given;
import static net.bytebuddy.utility.RandomString.make;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@DisplayName("Player controller")
public class PlayerControllerIT extends HttpBasedTest {

    @Autowired
    PlayerRepository playerRepository;

    @Test
    @DisplayName("Register player - ok")
    void registerPlayer_ok() throws JSONException {
        String name = make();
        Long gameId = generateId();
        var registerPlayerJSON = registerPlayerJSON(name, gameId);

        given()
                .headers(defaultHeaders())
                .contentType(APPLICATION_JSON_VALUE)
                .body(registerPlayerJSON.toString())
                .when()
                .post(url(REGISTER_PLAYER_URI))
                .then()
                .statusCode(CREATED.value())
                .body("id", notNullValue())
                .body("name", equalTo(name))
                .body("gameId", equalTo(gameId.intValue()));
    }

    @Test
    @DisplayName("Register player - missing name")
    void registerPlayer_missingName() throws JSONException {
        var registerPlayerJSON = registerPlayerJSON(null, generateId());

        given()
                .headers(defaultHeaders())
                .contentType(APPLICATION_JSON_VALUE)
                .body(registerPlayerJSON.toString())
                .when()
                .post(url(REGISTER_PLAYER_URI))
                .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Player details - ok")
    void getPlayerDetails_ok() {
        var player = createRandomPlayer();

        given()
                .headers(defaultHeaders())
                .when()
                .get(url(PLAYER_URI), player.getId())
                .then()
                .statusCode(OK.value())
                .body("id", equalTo(player.getId().intValue()))
                .body("name", equalTo(player.getName()))
                .body("gameId", equalTo(player.getGameId().intValue()));
    }

    @Test
    @DisplayName("Player details - not found")
    void playerDetails_notFound() {
        given()
                .headers(defaultHeaders())
                .when()
                .get(url(PLAYER_URI), generateId())
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    @DisplayName("Delete player - ok")
    void deletePlayer_ok() {
        var player = createRandomPlayer();

        given()
                .headers(defaultHeaders())
                .when()
                .delete(url(PLAYER_URI), player.getId())
                .then()
                .statusCode(NO_CONTENT.value());
    }

    @Test
    @DisplayName("Delete player - not found")
    void deletePlayer_notFound() {
        given()
                .headers(defaultHeaders())
                .when()
                .delete(url(PLAYER_URI), generateId())
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    @DisplayName("Update player game - ok")
    void updatePlayerGame_ok() throws JSONException {
        var player = createRandomPlayer();
        var updatePlayerGameJSON = updatePlayerGameJSON();
        int newGameId = updatePlayerGameJSON.getInt("gameId");

        given()
                .headers(defaultHeaders())
                .contentType(APPLICATION_JSON_VALUE)
                .body(updatePlayerGameJSON.toString())
                .when()
                .patch(url(PLAYER_URI), player.getId())
                .then()
                .statusCode(OK.value())
                .body("id", equalTo(player.getId().intValue()))
                .body("name", equalTo(player.getName()))
                .body("gameId", equalTo(newGameId));
    }

    @Test
    @DisplayName("Update player game - missing game ID")
    void updatePlayerGame_missingGameId() throws JSONException {
        var updatePlayerGameJSON = updatePlayerGameJSON()
                .put("gameId", null);

        given()
                .headers(defaultHeaders())
                .contentType("application/json")
                .body(updatePlayerGameJSON.toString())
                .when()
                .patch(url(PLAYER_URI), generateId())
                .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Update player game - player not found")
    void updatePlayerGame_playerNotFound() throws JSONException {
        var updatePlayerGameJSON = updatePlayerGameJSON();

        given()
                .headers(defaultHeaders())
                .contentType("application/json")
                .body(updatePlayerGameJSON.toString())
                .when()
                .patch(url(PLAYER_URI), generateId())
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    @DisplayName("Get game IDs - ok, found all")
    void getGameIds_okFoundAll() {
        String name = make();
        var player1 = createPlayer(name);
        createRandomPlayer();
        var player3 = createPlayer(name);

        given()
                .headers(defaultHeaders())
                .when()
                .get(url(PLAYERS_GAMES_URI), name)
                .then()
                .statusCode(OK.value())
                .body("games", containsInAnyOrder(player1.getGameId().intValue(), player3.getGameId().intValue()));
    }

    @Test
    @DisplayName("Get game IDs - ok, found none")
    void getGameIds_okFoundNone() {
        String name = make();
        createPlayer(name);
        createPlayer(name);
        createRandomPlayer();

        given()
                .headers(defaultHeaders())
                .when()
                .get(url(PLAYERS_GAMES_URI), make())
                .then()
                .statusCode(OK.value())
                .body("games", empty());
    }

    @Test
    @DisplayName("Remove game for players - ok, game found and removed")
    void removeGameForPlayers_okGameFoundAndRemoved() {
        var gameId = generateId();
        var player1 = createPlayer(gameId);
        var player2 = createPlayer(gameId);

        given()
                .headers(defaultHeaders())
                .when()
                .put(url(GAME_URI), gameId)
                .then()
                .statusCode(OK.value());

        var updatedPlayer1 = playerRepository.findById(player1.getId()).orElseThrow();
        var updatedPlayer2 = playerRepository.findById(player2.getId()).orElseThrow();

        assertAll(
                () -> assertThat(updatedPlayer1.getGameId()).isNull(),
                () -> assertThat(updatedPlayer2.getGameId()).isNull()
        );
    }

    @Test
    @DisplayName("Remove game for players - ok, game not found")
    void removeGameForPlayers_okGameNotFound() {
        var gameId = generateId();
        var player1 = createPlayer(gameId);
        var player2 = createPlayer(gameId);

        given()
                .headers(defaultHeaders())
                .when()
                .put(url(GAME_URI), generateId())
                .then()
                .statusCode(OK.value());

        var updatedPlayer1 = playerRepository.findById(player1.getId()).orElseThrow();
        var updatedPlayer2 = playerRepository.findById(player2.getId()).orElseThrow();

        assertAll(
                () -> assertThat(updatedPlayer1.getGameId()).isNotNull(),
                () -> assertThat(updatedPlayer2.getGameId()).isNotNull()
        );
    }

    Player createRandomPlayer() {
        return createPlayer(make());
    }

    Player createPlayer(String name) {
        return playerRepository.save(new Player()
                .setName(name)
                .setGameId(generateId()));
    }

    Player createPlayer(Long gameId) {
        return playerRepository.save(new Player()
                .setName(make())
                .setGameId(gameId));
    }

    JSONObject registerPlayerJSON() throws JSONException {
        return registerPlayerJSON(make(), generateId());
    }

    JSONObject registerPlayerJSON(String name, Long gameId) throws JSONException {
        return new JSONObject()
                .put("name", name)
                .put("gameId", gameId);
    }

    JSONObject updatePlayerGameJSON() throws JSONException {
        return new JSONObject()
                .put("gameId", generateId());
    }
}
