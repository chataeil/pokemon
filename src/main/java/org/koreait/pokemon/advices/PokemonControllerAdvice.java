package org.koreait.pokemon.advices;

import lombok.RequiredArgsConstructor;
//import org.koreait.mypokemon.services.MyPokemonService;
import org.koreait.wishlist.constants.WishType;
import org.koreait.wishlist.services.WishService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@RequiredArgsConstructor
@ControllerAdvice("org.koreait.pokemon")
public class PokemonControllerAdvice {
    private final WishService wishService;
//    private final MyPokemonService pokemonService;

    @ModelAttribute("myPokemons")
    public List<Long> myPokemons() {
        return wishService.getMyWish(WishType.POKEMON);
    }
//    @ModelAttribute("myEntrys")
//    public List<Long> myEntrys(){
//        return pokemonService.getMyPokemon();
//    }
}
