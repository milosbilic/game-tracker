package com.numarics.player.model.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePlayerGameRequestDto(@NotNull Long gameId) {
}
