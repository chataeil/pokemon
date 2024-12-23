package org.koreait.member.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.koreait.member.MemberInfo;
import org.koreait.member.libs.MemberUtil;
import org.koreait.member.services.MemberInfoService;
import org.koreait.member.services.MemberUpdateService;
import org.koreait.member.validators.JoinValidator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@ApplyErrorPage
@RequestMapping("/member")
@RequiredArgsConstructor
@SessionAttributes({"requestAgree", "requestLogin", "authCodeVerified"}) // 데이터 유지 세션쪽 속성 추가
public class MemberController {

    private final Utils utils;
    private final JoinValidator joinValidator; // 회원 가입 검증
    private final MemberUpdateService updateService; // 회원 가입 처리
    private final MemberInfoService infoService; // 회원 정보 조회
    private final MemberUtil memberUtil; //
    private final HttpSession session;

    @ModelAttribute("requestAgree") // 속성을 넣어서 데이터를 뷰로 전달할때.
    public RequestAgree requestAgree() {
        return new RequestAgree();
    }

    @ModelAttribute("requestLogin")
    public RequestLogin requestLogin() {
        return new RequestLogin();
    }

    // 이메일 인증 여부
    @ModelAttribute("authCodeVerified")
    public boolean authCodeVerified(){
        return false;
    }

    /* 회원 페이지 CSS */
    @ModelAttribute("addCss")
    public List<String> addCss() {
        return List.of("member/style");
    }

    @GetMapping("/login")
    public String login(@ModelAttribute RequestLogin form, Errors errors, Model model) {
        commonProcess("login", model); // 로그인 페이지 공통 처리

        if (form.getErrorCodes() != null) { // 검증 실패
            form.getErrorCodes().stream().map(s -> s.split("_"))
                    .forEach(s -> {
                        if (s.length > 1) {
                            errors.rejectValue(s[1], s[0]);
                        } else {
                            errors.reject(s[0]);
                        }
                    });
        }

        return utils.tpl("member/login");
    }

//    @ResponseBody
//    @GetMapping("/test")
//    public void test(){
//        MemberInfo memberInfo = (MemberInfo)SecurityContextHolder.getContext()
//                .getAuthentication().getPrincipal();
//        System.out.println(memberInfo);
/*    public void test(@AuthenticationPrincipal MemberInfo memberInfo){
        System.out.println(memberInfo);*/
//        System.out.println(SecurityContextHolder.getContext()
//                .getAuthentication().getPrincipal()); // 미로그인 상태 anonymousUser 문자열
//    }
 /*   public void test(Principal principal){
        String email = principal.getName();
        System.out.println(email);
    }*/
    /**
     * 회원가입 약관 동의
     *
     * @return
     */
    @GetMapping("/agree")
    public String joinAgree(Model model) {
        commonProcess("agree", model);

        return utils.tpl("member/agree");
    }

    /**
     * 회원 가입 양식 페이지
     * - 필수 약관 동의 여부 검증
     *
     * @return
     */
    @PostMapping("/join")
    public String join(RequestAgree agree, Errors errors, @ModelAttribute RequestJoin form, Model model) {
        commonProcess("join", model); // 회원 가입 공통 처리
        //
        // 회원가입 양식 첫 유입에서는 이메일인증 상태를 false
        model.addAttribute("authCodeVerified", false);

        joinValidator.validate(agree, errors);


        if (errors.hasErrors()) { // 약관 동의를 하지 않았다면 약관 동의 화면을 출력
            return utils.tpl("member/agree");
        }

        return utils.tpl("member/join");
    }

    /***
     * 회원가입 처리
     *
     * @return
     */
    @PostMapping("/join_ps")
    public String joinPs(@SessionAttribute("requestAgree")/* 멀티 페이지일땐 데이터를 공유할 수 없기 때문에 범위를 넓힘 기존 데이터 유지하면 다음 페이지 넘길때*/ RequestAgree agree, @Valid RequestJoin form, Errors errors, SessionStatus status, Model model) {
        commonProcess("join", model); // 회원가입 공통 처리

        joinValidator.validate(agree, errors); // 약관 동의 여부 체크
        joinValidator.validate(form, errors); // 회원 가입 양식 검증

        if (errors.hasErrors()) {
            return utils.tpl("member/join");
        }

        // 회원 가입 처리
        form.setRequiredTerms1(agree.isRequiredTerms1());
        form.setRequiredTerms2(agree.isRequiredTerms2());
        form.setRequiredTerms3(agree.isRequiredTerms3());
        form.setOptionalTerms(agree.getOptionalTerms());

        updateService.process(form);

        status.setComplete();

        // 회원가입 처리 완료 후 - 로그인 페이지로 이동
        return "redirect:/member/login";
    }

    @ResponseBody
    @GetMapping("/refresh")
    @PreAuthorize("isAuthenticated()") // 메서드 시큐리티 메서드 실행 전 권한체크
    public void refresh(Principal principal, HttpSession session){ // 인증된 사용자 정보

        MemberInfo memberInfo = (MemberInfo) infoService.loadUserByUsername(principal.getName());
        session.setAttribute("member", memberInfo.getMember());
    }
    /**
     * 공통 처리 부분
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "login";

        String pageTitle = null; // 페이지 제목
        List<String> addCommonScript = new ArrayList<>(); // 공통 자바스크립트
        List<String> addScript = new ArrayList<>(); // front쪽에 추가하는 자바스크립트

        if (mode.equals("login")) {  // 로그인 공통 처리
            pageTitle = utils.getMessage("로그인");

        } else if (mode.equals("join")) { // 회원가입 공통 처리
            pageTitle = utils.getMessage("회원가입");
            addCommonScript.add("address");
            addCommonScript.add("emailAuth");

            addScript.add("member/join");

        } else if (mode.equals("agree")) {
            pageTitle = utils.getMessage("약관동의");
            // 약관 동의 페이지에 최초 접근시 약관 선택을 초기화
            model.addAttribute("requestAgree", requestAgree());

        }


        // 페이지 제목
        model.addAttribute("pageTitle", pageTitle);

        // 공통 스크립트
        model.addAttribute("addCommonScript", addCommonScript);

        // front 스크립트
        model.addAttribute("addScript", addScript);
    }
}