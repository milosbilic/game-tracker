package com.numarics.player.converter;

import com.numarics.player.mapper.PlayerMapper;
import com.numarics.player.model.dto.PlayerDetailsDto;
import com.numarics.player.model.entity.Player;
import org.springframework.core.convert.converter.Converter;

public class PlayerToPlayerDetailsDtoConverter implements Converter<Player, PlayerDetailsDto> {

    @Override
    public PlayerDetailsDto convert(Player source) {
        return PlayerMapper.INSTANCE.mapToDetailsDto(source);
    }
}
