package com.numarics.game.repository.impl;

import com.numarics.game.model.entity.Game;
import com.numarics.game.model.filter.GameSearchFilter;
import com.numarics.game.repository.GameRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class GameRepositoryCustomImpl implements GameRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<Game> search(GameSearchFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Game> query = cb.createQuery(Game.class);
        Root<Game> root = query.from(Game.class);

        var predicates = new ArrayList<Predicate>();
        if (!filter.getGameIds().isEmpty()) {
            predicates.add(cb.in(root.get("id")).value(filter.getGameIds()));
        } else {
            ofNullable(filter.getStatus())
                    .ifPresent(status -> predicates.add(cb.equal(root.get("status"), status)));
            ofNullable(filter.getName())
                    .ifPresent(name -> predicates.add(cb.equal(root.get("name"), name)));
        }
        query.select(root);
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[]{}));
        }

        return entityManager.createQuery(query)
                .getResultList();
    }
}
