package org.koreait.mypokemon.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.global.libs.Utils;
import org.koreait.mypokemon.services.MyPokemonService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequiredArgsConstructor
public class MyPokemonController {

    private final HttpServletRequest request;
    private final MyPokemonService service;


    @GetMapping({"/add", "/remove"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void process(@RequestParam("seq") Long seq) {
        String mode = request.getRequestURI().contains("/remove") ? "remove" : "add";

        service.process(mode, seq);
    }

}
