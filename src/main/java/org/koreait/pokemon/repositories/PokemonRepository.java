package org.koreait.pokemon.repositories;

import org.koreait.pokemon.entities.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface PokemonRepository extends JpaRepository<Pokemon, Long>, QuerydslPredicateExecutor<Pokemon> { //제네릭타입은 래퍼클래스만 됨 참조만 가능.
    List<Pokemon> findByTypes(String type);
}
