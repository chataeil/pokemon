package org.koreait.pokemon.tests;

import org.junit.jupiter.api.Test;
import org.koreait.pokemon.api.services.ApiImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApiTest4 {
    @Autowired
    private ApiImageService service;

    @Test
    void updateTest1() {
        service.updateImages();
    }
}
