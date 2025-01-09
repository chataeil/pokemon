package org.koreait.wishlist.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.wishlist.constants.WishType;
import org.koreait.wishlist.services.WishService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController //JSON 형식 또는 XML 형식으로 직접 반환할 때 사용되며, View를 반환하는 대신 데이터를 반환합니다.
@RequiredArgsConstructor
@RequestMapping("/api/wish")
public class ApiWishController {

    private final HttpServletRequest request;
    private final WishService service;

    @GetMapping({"/add", "/remove"})
    @ResponseStatus(HttpStatus.NO_CONTENT) // 응답 상태 코드
    public void process(@RequestParam("seq") Long seq, @RequestParam("type") WishType type) { // 요청한 파라미터
        String mode = request.getRequestURI().contains("/remove") ? "remove" : "add"; // URI를 가져와서 포함되어 있는거 찾음

        service.process(mode, seq, type);
    }
}
