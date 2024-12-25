package org.koreait.pokemon.api.services;

import lombok.RequiredArgsConstructor;
import org.koreait.pokemon.api.entities.ApiPokemon;
import org.koreait.pokemon.api.entities.ApiTypeResponse;
import org.koreait.pokemon.api.entities.UrlItem;
import org.koreait.pokemon.entities.Pokemon;
import org.koreait.pokemon.repositories.TypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiTypeService {
    private final RestTemplate tpl;
    private final TypeRepository repository;

    public void updateTypes(){
        String url = "https://pokeapi.co/api/v2/type/";
        ApiTypeResponse response = tpl.getForObject(URI.create(url), ApiTypeResponse.class);
        List<UrlItem> items = response.getResults();
        if (items == null || items.isEmpty()) {
            return;
        }
        List<Pokemon> pokemons = new ArrayList<>();
        for (UrlItem item : items) {
            Pokemon pokemon = new Pokemon();
            // 기초 데이터
            ApiPokemon data1 = tpl.getForObject(URI.create(item.getUrl()), ApiPokemon.class);
            pokemon.setSeq(Long.valueOf(data1.getId()));
            String types = data1.getTypes().stream().map(d -> d.getType().getName())
                    .collect(Collectors.joining("||"));  // 타입1||타입2||타입3
            pokemon.setTypes(types);
            pokemons.add(pokemon);
        }

        // DB에 저장
        repository.saveAllAndFlush(pokemons);
    }
}