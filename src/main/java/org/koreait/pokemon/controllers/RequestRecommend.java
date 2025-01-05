package org.koreait.pokemon.controllers;

import lombok.Data;


import java.io.Serializable;


@Data
public class RequestRecommend implements Serializable {

    private String selectTerms; // 단일 필드로 수정
}
