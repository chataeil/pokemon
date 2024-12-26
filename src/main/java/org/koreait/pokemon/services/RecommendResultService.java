package org.koreait.pokemon.services;

import lombok.RequiredArgsConstructor;
import org.koreait.pokemon.entities.Pokemon;
import org.koreait.pokemon.entities.PokemonType;
import org.koreait.pokemon.repositories.PokemonRepository;
import org.koreait.pokemon.repositories.TypeRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendResultService {

private final TypeRepository repository;

public List<PokemonType> getRecommendType() {
    return repository.findAll();
    }
}