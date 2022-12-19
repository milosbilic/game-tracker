package com.numarics.player.model.dto;

import jakarta.validation.constraints.NotNull;

public record RegisterPlayerRequestDto(@NotNull String name, Long gameId) {
}
