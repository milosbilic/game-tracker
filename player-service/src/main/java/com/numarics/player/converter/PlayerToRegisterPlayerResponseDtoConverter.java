package com.numarics.player.converter;

import com.numarics.player.mapper.PlayerMapper;
import com.numarics.player.model.dto.RegisterPlayerResponseDto;
import com.numarics.player.model.entity.Player;
import org.springframework.core.convert.converter.Converter;

public class PlayerToRegisterPlayerResponseDtoConverter implements Converter<Player, RegisterPlayerResponseDto> {

    @Override
    public RegisterPlayerResponseDto convert(Player source) {
        return PlayerMapper.INSTANCE.mapToRegisterPlayerResponseDto(source);
    }
}
