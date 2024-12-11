package org.koreait.pokemon.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class Types {
    private int slot;
    private UrlItem type;
}
