package org.koreait.member.libs;

import lombok.Setter;
import org.koreait.member.MemberInfo;
import org.koreait.member.constants.Authority;
import org.koreait.member.entities.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Setter
@Component
public class MemberUtil {

    private Member member;

    public boolean isLogin(){
        return getMember() != null;
    }

    /**
     * 관리자 여부
     *  권한 - MANAGER, ADMIN
     * @return
     */
    public boolean isAdmin(){
        return isLogin() &&
                getMember().getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority() == Authority.ADMIN || a.getAuthority() == Authority.MANGER);
    }
    /**
     * 로그인 한 회원의 정보 조회
     *
     * @return
     */
    public Member getMember(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //★auth 핵심★ 인증을 해야 만들어짐. 인증된 객체 현재 로그인한 놈 정보
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof
                MemberInfo memberInfo){
            if (member == null){
                setMember(memberInfo.getMember());
                return member;
            } else {
                return member;
            }
        }

            return null;
    }
}
