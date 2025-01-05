package org.koreait.pokemon.repositories;

import org.koreait.pokemon.entities.Pokemon;
import org.koreait.pokemon.entities.PokemonImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ImageRepository extends JpaRepository<PokemonImage, Long>, QuerydslPredicateExecutor<Pokemon> {
}
