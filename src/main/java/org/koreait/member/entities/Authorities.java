package org.koreait.member.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koreait.member.constants.Authority;

import java.io.Serializable;

@Data
@Entity
@IdClass(AuthoritiesID.class) // 이거 추가해야됨
@NoArgsConstructor
@AllArgsConstructor
public class Authorities implements Serializable {
    @Id
    @ManyToOne(fetch = FetchType.LAZY) // 첨엔 로딩 안했다가 사용할 때 로딩
    private Member member;


    @Id
    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private Authority authority;
}
