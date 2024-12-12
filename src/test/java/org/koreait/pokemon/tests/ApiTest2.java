package org.koreait.pokemon.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApiTest2 {
    @Autowired
    private org.koreait.pokemon.api.services.ApiUpdateService service;

    @Test
    void updateTest1(){
        for (int i = 0; i < 6; i++) {
            service.update(i);
        }
    }
}
