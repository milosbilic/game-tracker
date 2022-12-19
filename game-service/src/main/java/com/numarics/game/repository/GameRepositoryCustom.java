package com.numarics.game.repository;

import com.numarics.game.model.entity.Game;
import com.numarics.game.model.filter.GameSearchFilter;

import java.util.List;

public interface GameRepositoryCustom {

    List<Game> search(GameSearchFilter filter);
}
