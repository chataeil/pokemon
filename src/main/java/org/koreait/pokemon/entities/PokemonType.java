package org.koreait.pokemon.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class PokemonType {

    @Id
    @GeneratedValue
    private Long seq;

    private String type;

}
