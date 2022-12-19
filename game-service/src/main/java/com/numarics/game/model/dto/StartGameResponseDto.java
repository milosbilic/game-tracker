package com.numarics.game.model.dto;

import com.numarics.game.model.entity.Game;

import java.time.Instant;

public record StartGameResponseDto(Long id,
                                   String name,
                                   Game.Status status,
                                   Instant createdAt,
                                   Instant updatedAt) {
}
