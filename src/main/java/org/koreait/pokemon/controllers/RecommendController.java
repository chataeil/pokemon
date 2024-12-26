package org.koreait.pokemon.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.global.libs.Utils;
import org.koreait.pokemon.api.services.ApiUpdateService;
import org.koreait.pokemon.entities.PokemonType;
import org.koreait.pokemon.services.RecommendResultService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pokemon")
@SessionAttributes("RequestRecommend")
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendResultService resultService;
    private final Utils utils;

    @ModelAttribute("requestRecommend")
    public RequestRecommend recommend() {
        return new RequestRecommend();
    }

    @GetMapping("/recommend")
    public String recommend( Model model) {
        List<PokemonType> types = resultService.getRecommendType();
        model.addAttribute("types", types);
        commonProcess("recommend", model);

        return utils.tpl("pokemon/recommend");
    }

    @PostMapping("/recommend")
    public String recommend_ps(RequestRecommend recommend, Model model){
        List<String> selectedTypes = new ArrayList<>();

        selectedTypes.add(recommend.getSelectTerms1());
        selectedTypes.add(recommend.getSelectTerms2());
        selectedTypes.add(recommend.getSelectTerms3());
        selectedTypes.add(recommend.getSelectTerms4());
        selectedTypes.add(recommend.getSelectTerms5());
        selectedTypes.add(recommend.getSelectTerms6());
        selectedTypes.add(recommend.getSelectTerms7());
        selectedTypes.add(recommend.getSelectTerms8());
        selectedTypes.add(recommend.getSelectTerms9());
        selectedTypes.add(recommend.getSelectTerms10());
        selectedTypes.add(recommend.getSelectTerms11());
        selectedTypes.add(recommend.getSelectTerms12());
        selectedTypes.add(recommend.getSelectTerms13());
        selectedTypes.add(recommend.getSelectTerms14());
        selectedTypes.add(recommend.getSelectTerms15());
        selectedTypes.add(recommend.getSelectTerms16());
        selectedTypes.add(recommend.getSelectTerms17());
        selectedTypes.add(recommend.getSelectTerms18());

        model.addAttribute("selectedTypes", selectedTypes);
        return utils.tpl("pokemon/recommendresult");
    }

    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "main";
        String pageTitle = utils.getMessage("타입 추천");

        List<String> addCss = new ArrayList<>();
        List<String> addCommonScript = new ArrayList<>();
        addCss.add("pokemon/style");
        addCommonScript.add("recommend");

        if (mode.equals("recommend")) {
            pageTitle = utils.getMessage("나의 타입");
        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonScript", addCommonScript);
    }
}