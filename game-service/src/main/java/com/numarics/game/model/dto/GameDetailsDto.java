package com.numarics.game.model.dto;

import com.numarics.game.model.entity.Game.Status;

import java.time.Instant;

public record GameDetailsDto(Long id,
                             String name,
                             Status status,
                             Instant createdAt,
                             Instant updatedAt) {
}
