package org.koreait.member.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.koreait.member.controllers.RequestLogin;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        HttpSession session = request.getSession();
        RequestLogin form = (RequestLogin) Objects.requireNonNullElse(session.getAttribute("requestLogin"), new RequestLogin()); // null이면 새로 만들고 null이 아니면 기본값.
        form.setErrorCodes(null); // 세션 범위이기 때문에 데이터가 유지됨. 에러코드는 새로 검증해야 해서 초기화 시킴.



        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String redirectUrl = request.getContextPath() + "/member/login"; // 요청한 경로

        form.setEmail(email);
        form.setPassword(password);

            // 아이디 또는 비밀번호를 입력하지 않은 경우, 아이디로 조회 X, 비번이 일치하지 않는 경우
            if (exception instanceof BadCredentialsException) {
                List<String> errorCodes = Objects.requireNonNullElse(form.getErrorCodes(), new
                        ArrayList<>());

                if (!StringUtils.hasText(email)){
                    errorCodes.add("NotBlank_email");
                }

                if (!StringUtils.hasText(password)){
                    errorCodes.add("NotBlank_password");
                }

                if (errorCodes.isEmpty()){
                    errorCodes.add("Failure.validate.login");
                }

                form.setErrorCodes(errorCodes);
            } else if (exception instanceof CredentialsExpiredException) { // 비밀번호가 만료된 경우

                    redirectUrl = request.getContextPath()+"/member/password/change";
            } else if (exception instanceof DisabledException) { //탈퇴한 회원
                form.setErrorCodes(List.of("Failure.disabled.login"));

            }

        System.out.println(exception);

            session.setAttribute("requestLogin", form);
            // 로그인 실패시에는 로그인 페이지로 이동
        response.sendRedirect(redirectUrl);
    }
}
