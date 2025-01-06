package org.koreait.mypokemon.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.member.entities.Member;

@Data
@Entity
@IdClass(MyPokemon.class)
public class MyPokemon {
    @Id
    private Long seq;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
