package com.numarics.game.repository;

import com.numarics.game.model.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryCustom {

}
