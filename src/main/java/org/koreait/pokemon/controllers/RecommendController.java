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
    public String recommend(Model model) {
        List<PokemonType> types = resultService.getRecommendType();
        model.addAttribute("types", types);
        commonProcess("recommend", model);

        return utils.tpl("pokemon/recommend");
    }

    @PostMapping("/recommend")
    public String recommend_ps(@ModelAttribute("requestRecommend") RequestRecommend recommend, Model model) {
        String selectedType = recommend.getSelectTerms(); // 단일 필드로 선택된 값 가져오기

        if (selectedType == null || selectedType.isEmpty()) {
            model.addAttribute("error", "타입을 선택해주세요.");
            return utils.tpl("pokemon/recommend");
        }

        model.addAttribute("selectedType", selectedType);
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