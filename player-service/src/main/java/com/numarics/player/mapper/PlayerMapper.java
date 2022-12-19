package com.numarics.player.mapper;

import com.numarics.player.model.dto.PlayerDetailsDto;
import com.numarics.player.model.dto.RegisterPlayerResponseDto;
import com.numarics.player.model.entity.Player;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    PlayerMapper INSTANCE = Mappers.getMapper(PlayerMapper.class);

    RegisterPlayerResponseDto mapToRegisterPlayerResponseDto(Player player);

    PlayerDetailsDto mapToDetailsDto(Player player);
}
