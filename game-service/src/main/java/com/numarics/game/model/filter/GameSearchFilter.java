package com.numarics.game.model.filter;

import com.numarics.game.model.entity.Game;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public final class GameSearchFilter {
    private Game.Status status;
    private String name;
    private String playerName;
    private List<Long> gameIds;
}
