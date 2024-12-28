package org.koreait.pokemon.api.services;

import lombok.RequiredArgsConstructor;
import org.koreait.pokemon.repositories.TypeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PokemonRecommendService {
    private final TypeRepository repository;


}
