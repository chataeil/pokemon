package org.koreait.pokemon.services;

import lombok.RequiredArgsConstructor;
import org.koreait.pokemon.entities.Pokemon;
import org.koreait.pokemon.repositories.PokemonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PokemonImageService {
    private final PokemonRepository repository;

    public List<Pokemon> getImage(){
        return repository.findAll();
    }
}
