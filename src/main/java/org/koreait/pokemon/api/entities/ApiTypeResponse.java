package org.koreait.pokemon.api.entities;

import lombok.Data;

import java.util.List;

@Data
public class ApiTypeResponse {
    private List<UrlItem> results;
}
