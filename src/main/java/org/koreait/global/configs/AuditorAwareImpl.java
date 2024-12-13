package org.koreait.global.configs;

import lombok.RequiredArgsConstructor;
import org.koreait.member.libs.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
/* 감독하는 역할. 로그인한 사용자 정보 들어갈때 사용.*/
@Lazy
@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> { // 스트링 데이터가 들어가는 모든 건 AuditorAware<string> 인터페이스 사용.

    private final MemberUtil memberUtil;

    @Override
    public Optional<String> getCurrentAuditor() {
        String email = null;
        if (memberUtil.isLogin()){
            email = memberUtil.getMember().getEmail();
        }
        return Optional.ofNullable(email);
    }
}
