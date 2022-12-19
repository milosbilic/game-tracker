package com.numarics.game.mapper;

import com.numarics.game.model.dto.GameDetailsDto;
import com.numarics.game.model.dto.StartGameResponseDto;
import com.numarics.game.model.entity.Game;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameMapper {

    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    GameDetailsDto mapToDetails(Game game);

    StartGameResponseDto mapToStartGameResponseDto(Game game);
}
