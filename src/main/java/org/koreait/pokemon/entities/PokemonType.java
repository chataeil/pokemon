package org.koreait.pokemon.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
public class PokemonType implements Serializable {

    @Id
    @GeneratedValue
    private Long seq;

//    @Id
    private String type;

//    @OneToMany(mappedBy = "type")
//    private List<Pokemon> pokemons; // 이 타입을 가진 포켓몬들

}
