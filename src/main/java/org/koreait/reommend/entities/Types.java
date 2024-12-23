package org.koreait.reommend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "pokemon_types")
public class Types {
    @Id
    private Long seq;

    @Lob
    private String types;   // 타입1||타입2||타입3

    @Transient
    private List<String> _types;
}
