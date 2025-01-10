package org.koreait.mypage.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.koreait.global.paging.CommonSearch;
import org.koreait.global.paging.ListData;
import org.koreait.member.MemberInfo;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.koreait.member.services.MemberInfoService;
import org.koreait.member.services.MemberUpdateService;
import org.koreait.member.social.services.KakaoLoginService;
import org.koreait.mypage.validators.ProfileValidator;
import org.koreait.pokemon.controllers.PokemonSearch;
import org.koreait.pokemon.entities.Pokemon;
import org.koreait.pokemon.services.PokemonInfoService;
import org.koreait.wishlist.constants.WishType;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
@ApplyErrorPage
@RequestMapping("/mypage")
@RequiredArgsConstructor
@SessionAttributes("profile")
public class MypageController {
    private final Utils utils;
    private final MemberUtil memberUtil;
    private final ModelMapper modelMapper;
    private final MemberUpdateService updateService;
    private final ProfileValidator profileValidator;
    private final MemberInfoService infoService;
    private final PokemonInfoService pokemonInfoService;
    private final KakaoLoginService kakaoLoginService;

    @ModelAttribute("profile")
    public Member getMember() {
        return memberUtil.getMember();
    }

    @GetMapping
    public String index(Model model) {
        commonProcess("main", model);

        return utils.tpl("mypage/index");
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        commonProcess("profile", model);

        Member member = memberUtil.getMember();
        RequestProfile form = modelMapper.map(member, RequestProfile.class);
        String optionalTerms = member.getOptionalTerms();
        if (StringUtils.hasText(optionalTerms)) {
            form.setOptionalTerms(Arrays.stream(optionalTerms.split("\\|\\|")).toList());
        }


        form.setKakaoLoginConnectUrl(kakaoLoginService.getLoginUrl("connect"));
        form.setKakaoLoginDisconnectUrl(kakaoLoginService.getLoginUrl("disconnect"));

        model.addAttribute("requestProfile", form);

        return utils.tpl("mypage/profile");
    }

    @PatchMapping("/profile")
    public String updateProfile(@Valid RequestProfile form, Errors errors, Model model) {
        commonProcess("profile", model);

        profileValidator.validate(form, errors);

        if (errors.hasErrors()) {
            return utils.tpl("mypage/profile");
        }

        updateService.process(form);

        // 프로필 속성 변경
        model.addAttribute("profile", memberUtil.getMember());

        return "redirect:/mypage"; // 회원 정보 수정 완료 후 마이페이지 메인 이동
    }

    @ResponseBody
    @GetMapping("/refresh")
    public void refresh(Principal principal, Model model, HttpSession session) {

        MemberInfo memberInfo = (MemberInfo) infoService.loadUserByUsername(principal.getName());
        session.setAttribute("member", memberInfo.getMember());

        model.addAttribute("profile", memberInfo.getMember());
    }

    /**
     * 찜하기 목록
     *
     * @param mode : POKEMON : 포켓몬 찜하기 목록, BOARD : 게시글 찜하기 목록
     * @return
     */
    @GetMapping({"/wishlist", "/wishlist/{mode}"})
    public String wishlist(@PathVariable(name="mode", required = false) WishType mode, CommonSearch search, Model model) {
        commonProcess("wishlist", model);

        mode = Objects.requireNonNullElse(mode, WishType.POKEMON);
        if (mode == WishType.BOARD) { // 게시글 찜하기 목록

        } else if (mode == WishType.GAME_POKEMON) { // 나의 포켓몬 6마리

            List<Pokemon> items = pokemonInfoService.getMyPokemons(); // getMyList가 목록 데이터 반환
            model.addAttribute("items", items);

        } else { // 포켓몬 찜하기 목록
            PokemonSearch pSearch = modelMapper.map(search, PokemonSearch.class);
            ListData<Pokemon> data = pokemonInfoService.getMyPokemons(pSearch); // Board가 들어올지 Pokemon이 들어올지 모르기 때문에 둘다 들어올 수 있도록 해놓음. 들어온 값에 따라 다르게 처리. 그래서 commonsearch씀 (다형성)
            model.addAttribute("items", data.getItems()); // 목록 데이터
            model.addAttribute("pagination", data.getPagination());
        }

        return utils.tpl("mypage/wishlist");
    }
//    @GetMapping({"/mypokemonlist"})
//    public String mypokemonlist(CommonSearch search, Model model) {
//        commonProcess("mypokemonlist", model);
//
//        PokemonSearch pSearch = modelMapper.map(search, PokemonSearch.class);
//        List<Pokemon> items = pokemonInfoService.getMyEntrys(pSearch);
//
//        model.addAttribute("items", items);
//
//        return utils.tpl("mypage/mypokemonlist");
//    }

    /**
     * 컨트롤러 공통 처리 영역
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "main";
        String pageTitle = utils.getMessage("마이페이지");

        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        if (mode.equals("profile")) { // 회원정보 수정
            addCommonScript.add("fileManager"); // 공통적으로 필요한 스크립트
            addCommonScript.add("address"); // 공통으로 필요한 스크립트
            addScript.add("mypage/profile"); // 페이지 별로 필요한 스크립트.
            pageTitle = utils.getMessage("회원정보_수정");
        } else if (mode.equals("wishlist")) { // 찜하기 목록
            addCommonScript.add("wish");
            pageTitle = utils.getMessage("나의_WISH");
        }

        model.addAttribute("addCommonScript", addCommonScript); // 공통적으로 필요한 스크립트
        model.addAttribute("addScript", addScript); // 페이지별로 필요한 스크립트
        model.addAttribute("pageTitle", pageTitle);
    }
}