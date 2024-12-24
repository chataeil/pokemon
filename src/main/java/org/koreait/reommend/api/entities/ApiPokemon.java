package org.koreait.reommend.api.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 일치하지 않으면 무시하고 넘어감 필요한것만 정의 하려고
public class ApiPokemon {
    private int id;
    private String name;
    private Sprites sprites;
    private int weight;
    private int height;
    private List<Types> types;
    private List<Ability> abilities;

    @JsonAlias("base_experience")
    private int baseExperience;

    // https://pokeapi.co/api/v2/pokemon-species/1
    private List<Names> names;

    @JsonAlias("flavor_text_entries")
    private List<FlavorText> flavorTextEntries;

    private List<Genus> genera;
}