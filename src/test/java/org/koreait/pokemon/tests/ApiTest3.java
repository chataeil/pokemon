package org.koreait.pokemon.tests;

import org.junit.jupiter.api.Test;
import org.koreait.pokemon.api.services.ApiTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApiTest3 {
    @Autowired
    private ApiTypeService service;

    @Test
    void updateTest1() {
    service.updateTypes();
  }
}
