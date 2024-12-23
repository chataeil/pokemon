package org.koreait.reommend.services;

import lombok.RequiredArgsConstructor;
import org.koreait.pokemon.exceptions.PokemonNotFoundException;
import org.koreait.reommend.entities.Types;
import org.koreait.reommend.repositories.TypeRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;


@Lazy
@Service
@RequiredArgsConstructor
public class RecommendServices {

//    private final TypeRepository repository;
//
//    public Types get(Long seq){
//        Types item = repository.findById(seq).orElseThrow(PokemonNotFoundException::new);
//
//        addInfo(item);
//        return item;
//    }
//
//
//
//    private void addInfo(Types item) {
//        String types = item.getTypes();
//        if (StringUtils.hasText(types)) {
//            item.set_types(Arrays.stream(types.split("\\|\\|")).toList());
//        }
//    }
}