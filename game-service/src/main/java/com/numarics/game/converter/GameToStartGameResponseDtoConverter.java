package com.numarics.game.converter;

import com.numarics.game.mapper.GameMapper;
import com.numarics.game.model.dto.StartGameResponseDto;
import com.numarics.game.model.entity.Game;
import org.springframework.core.convert.converter.Converter;

public class GameToStartGameResponseDtoConverter implements Converter<Game, StartGameResponseDto> {

    @Override
    public StartGameResponseDto convert(Game source) {
        return GameMapper.INSTANCE.mapToStartGameResponseDto(source);
    }
}
