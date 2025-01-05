package org.koreait.mypage.controllers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.koreait.member.constants.Gender;
import org.koreait.pokemon.entities.Pokemon;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class RequestProfile {
    @NotBlank
    private  String name;

    @NotBlank
    private String nickName;

    @Size(min = 8, max = 20)
    private String password;

    private String confirmPassword;

    @NotNull
    private Gender gender;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDt; // 생년월일

    @NotBlank
    private String zipCode;

    @NotBlank
    private String address;

    private String addressSub;

    private List<String> optionalTerms; // 추가 선택 약관

    private List<String> frontImage;
}
