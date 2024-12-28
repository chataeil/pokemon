package org.koreait.pokemon.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class PokemonType {

    @Id
    @GeneratedValue
    private Long seq;

    @Id
    @OneToMany(mappedBy = "pokemons")
    private String type;

}
