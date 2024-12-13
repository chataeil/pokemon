package org.koreait.member.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.member.constants.Authority;

import java.io.Serializable;

@Data
@Entity
@IdClass(AuthoritiesID.class) // 이거 추가해야됨
public class Authorities implements Serializable {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;


    @Id
    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private Authority authority;
}
