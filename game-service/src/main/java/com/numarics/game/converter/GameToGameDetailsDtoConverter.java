package com.numarics.game.converter;

import com.numarics.game.mapper.GameMapper;
import com.numarics.game.model.dto.GameDetailsDto;
import com.numarics.game.model.entity.Game;
import org.springframework.core.convert.converter.Converter;

public class GameToGameDetailsDtoConverter implements Converter<Game, GameDetailsDto> {

    @Override
    public GameDetailsDto convert(Game source) {
        return GameMapper.INSTANCE.mapToDetails(source);
    }
}
