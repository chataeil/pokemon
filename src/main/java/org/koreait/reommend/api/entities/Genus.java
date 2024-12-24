package org.koreait.reommend.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Genus {
    private String genus;
    private UrlItem language;

}
