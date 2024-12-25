package org.koreait.pokemon.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.global.libs.Utils;
import org.koreait.pokemon.api.services.ApiUpdateService;
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
    private final ApiUpdateService service;
    private final Utils utils;

    @ModelAttribute("requestRecommend")
    public RequestRecommend recommend() {
        return new RequestRecommend();
    }

    @GetMapping("/recommend")
    public String recommend(@ModelAttribute RequestRecommend recommend, Model model) {
        commonProcess("recommend", model);

        return utils.tpl("pokemon/recommend");
    }
    @GetMapping("/your_type")
    public String your_type(){
        return null;
    }

    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "list";
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