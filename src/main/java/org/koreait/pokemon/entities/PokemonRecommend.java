package org.koreait.pokemon.entities;

<<<<<<< HEAD
import lombok.Data;
=======
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.ToString;
import org.koreait.pokemon.api.entities.ApiPokemon;

import java.util.List;
>>>>>>> 23279b73ec70c61d7570d59e9b2b5177b5f28b11

@Data
public class PokemonRecommend {

<<<<<<< HEAD
  // @ToString.Exclude
   // @ManyToOne
   // private List<ApiPokemon> pokemons;
=======
    @ToString.Exclude
    @ManyToOne
    private List<ApiPokemon> pokemons;
>>>>>>> 23279b73ec70c61d7570d59e9b2b5177b5f28b11

}
