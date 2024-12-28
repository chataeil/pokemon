package org.koreait.pokemon.entities;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.ToString;
import org.koreait.pokemon.api.entities.ApiPokemon;

import java.util.List;

@Data
public class PokemonRecommend {

    @ToString.Exclude
    @ManyToOne
    private List<ApiPokemon> pokemons;

}
