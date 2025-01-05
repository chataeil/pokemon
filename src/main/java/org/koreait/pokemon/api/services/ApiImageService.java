package org.koreait.pokemon.api.services;

import lombok.RequiredArgsConstructor;
import org.koreait.pokemon.api.entities.ApiPokemon;
import org.koreait.pokemon.api.entities.ApiTypeResponse;
import org.koreait.pokemon.api.entities.UrlItem;
import org.koreait.pokemon.entities.Pokemon;
import org.koreait.pokemon.entities.PokemonImage;
import org.koreait.pokemon.entities.PokemonType;
import org.koreait.pokemon.repositories.ImageRepository;
import org.koreait.pokemon.repositories.PokemonRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiImageService {

    private final RestTemplate tpl;
    private final ImageRepository repository;

    public void updateImages() {
        String url = "https://pokeapi.co/api/v2/pokemon?limit=1000";

        ApiTypeResponse response = tpl.getForObject(URI.create(url), ApiTypeResponse.class);
        List<UrlItem> items = response.getResults();

        if (items == null || items.isEmpty()) {
            return;
        }


        List<PokemonImage> pokemons = new ArrayList<>();

        for (UrlItem item : items) {
            ApiPokemon data1 = tpl.getForObject(URI.create(item.getUrl()), ApiPokemon.class);

            PokemonImage pokemon = new PokemonImage();
            pokemon.setSeq(Long.valueOf(data1.getId()));  // ID 설정
            pokemon.setNameEn(data1.getName()); // 영문 이름
            pokemon.setFrontImage(data1.getSprites().getOther().getOfficialArtwork().get("front_default")); // frontImage만 설정

            pokemons.add(pokemon);  // 리스트에 추가
            String url2 = String.format("https://pokeapi.co/api/v2/pokemon-species/%d", data1.getId());
            ApiPokemon data2 = tpl.getForObject(URI.create(url2), ApiPokemon.class);

            // 한글 이름
            String nameKr = data2.getNames().stream().filter(d -> d.getLanguage().getName().equals("ko")).map(d -> d.getName()).collect(Collectors.joining());
            pokemon.setName(nameKr);
        }

        // DB에 저장 (새로 삽입)
        repository.saveAllAndFlush(pokemons);
    }
}
