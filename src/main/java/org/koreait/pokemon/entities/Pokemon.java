package org.koreait.pokemon.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.global.entities.BaseEntity;

import java.util.List;

@Data
@Entity
public class Pokemon extends BaseEntity {
    @Id
    private Long seq;
    @Column(length = 50)
    private String name; // 한글 포켓몬 이름
    @Column(length = 50)
    private String nameEn; // 영어 포켓몬 이름
    private int weight;
    private int height;
    private int baseExperience;
    // 기본 데이터형일땐 not null 이 붙음 래퍼 클래스 형태의 Integer Long 을 사용시엔 not null 이 안붙음 필수가 아닌 경우 기본형이 아닌 래퍼 클래스로 정의하면 됨.
    private String frontImage;

    @Lob
    private String flavorText; // 설명

    private String types;   // 타입1||타입2||타입3
    private String abilities; // 능력1||능력2||능력3

    @Transient
    private List<String> _types;

    @Transient
    private List<String> _abilities;
}
