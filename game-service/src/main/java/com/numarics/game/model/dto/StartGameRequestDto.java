package com.numarics.game.model.dto;

import jakarta.validation.constraints.NotNull;

public record StartGameRequestDto(@NotNull String name, Long playerId) {
}
