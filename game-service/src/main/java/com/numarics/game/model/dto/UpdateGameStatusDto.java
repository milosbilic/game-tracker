package com.numarics.game.model.dto;

import com.numarics.game.model.entity.Game;
import jakarta.validation.constraints.NotNull;

public record UpdateGameStatusDto(@NotNull Game.Status status) {
}
