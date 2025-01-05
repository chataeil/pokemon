package org.koreait.pokemon.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.global.entities.BaseEntity;

import java.util.List;
import java.util.Map;

@Data
@Entity
public class PokemonImage extends BaseEntity {
    @Id
    private Long seq;
    @Column(length = 50)
    private String name; // 한글 포켓몬 이름
    @Column(length = 50)
    private String nameEn; // 영어 포켓몬 이름

        private String frontImage;


}
