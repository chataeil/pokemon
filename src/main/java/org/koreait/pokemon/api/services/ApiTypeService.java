package org.koreait.pokemon.api.services;

import lombok.RequiredArgsConstructor;
import org.koreait.pokemon.api.entities.ApiTypeResponse;
import org.koreait.pokemon.api.entities.UrlItem;
import org.koreait.pokemon.entities.PokemonType;
import org.koreait.pokemon.repositories.TypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiTypeService {
    private final RestTemplate tpl;
    private final TypeRepository repository;
    /*private final PokemonRepository repository;*/

    public void updateTypes(){
        String url = "https://pokeapi.co/api/v2/type/";
        ApiTypeResponse response = tpl.getForObject(URI.create(url), ApiTypeResponse.class);
        List<UrlItem> items = response.getResults();
        if (items == null || items.isEmpty()) {
            return;
        }
        List<PokemonType> pokemons = new ArrayList<>();
        for (UrlItem item : items) {
            PokemonType pokemon = new PokemonType();
            // 기초 데이터

            pokemon.setType(item.getName());
            pokemons.add(pokemon);

/*            System.out.println(data1);
            pokemon.setSeq(Long.valueOf(data1.getId()));
            String types = data1.getTypes().stream().map(d -> d.getType().getName())
                    .collect(Collectors.joining("||"));  // 타입1||타입2||타입3*/
            //pokemon.setTypes(types);
            //pokemons.add(pokemon);
        }

        // DB에 저장
        repository.saveAllAndFlush(pokemons);
    }
}