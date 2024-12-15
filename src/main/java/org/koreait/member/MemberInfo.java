package org.koreait.member;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.koreait.member.entities.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Builder
@ToString
public class MemberInfo implements UserDetails {

    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // 권한 가지고 오는거
       return authorities;
    }

    @Override
    public String getPassword() { // 비밀번호
        return password;
    }

    @Override
    public String getUsername() { // 유저 이름 ID
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { // 계정 만료 여부.
        return true;
    }

    @Override 
    public boolean isAccountNonLocked() { // 계정 잠기 여부.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { // 비밀번호 변경 시점.
        LocalDateTime credentialChangedAt = member.getCredentialChangedAt();
        return credentialChangedAt != null &&
                credentialChangedAt.isAfter(LocalDateTime.now().minusMonths(1L));
    }

    @Override
    public boolean isEnabled() { // 회원 탈퇴 여부
        return member.getDeletedAt() == null;
    } // 회원 탈퇴 여부
}
