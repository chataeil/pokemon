package org.koreait.pokemon.repositories;

import org.koreait.pokemon.entities.PokemonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface TypeRepository extends JpaRepository<PokemonType, Long>, QuerydslPredicateExecutor<PokemonType> {
}
