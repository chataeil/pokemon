package org.koreait.member.service;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.koreait.member.constants.Gender;
import org.koreait.member.controllers.RequestJoin;
import org.koreait.member.entities.Authorities;
import org.koreait.member.entities.Member;
import org.koreait.member.entities.QAuthorities;
import org.koreait.member.repositories.AuthoritiesRepository;
import org.koreait.member.repositories.MemberRepository;
import org.koreait.member.services.MemberUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@SpringBootTest
@ActiveProfiles({"default", "test"})
public class MemberUpdateServiceTest {

    @Autowired
    private MemberUpdateService updateService;

    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    @Autowired
    private MemberRepository memberRepository;

    private RequestJoin form;

    @BeforeEach
    void init(){
        Faker faker = new Faker(Locale.KOREAN);

        form = new RequestJoin();
        form.setEmail(faker.internet().emailAddress());
        form.setPassword("_aA123456");
        form.setName(faker.name().name());
        form.setBirthDt(LocalDate.now().minusYears(20L));
        form.setZipCode(faker.address().zipCode());
        form.setAddress(faker.address().fullAddress());
        form.setAddressSub(faker.address().buildingNumber());
        form.setNickName(faker.name().name());
        form.setGender(Gender.MALE);
        form.setRequiredTerms1(true);
        form.setRequiredTerms2(true);
        form.setRequiredTerms3(true);
        form.setOptionalTerms(List.of("advertisement"));
        System.out.println(form);
    }
    @Test
    @DisplayName("회원가입 기능 테스트")
    void joinTest(){

        updateService.process(form);
        Member member = memberRepository.findByEmail(form.getEmail()).orElse(null);
        QAuthorities qAuthorities = QAuthorities.authorities;
        List<Authorities> items =  (List<Authorities>)authoritiesRepository
                .findAll(qAuthorities.member.eq(member));

    }
    @Test
    void authoritiesTest(){
        updateService.process(form);
        Member member = memberRepository.findByEmail(form.getEmail()).orElse(null);
        QAuthorities qAuthorities = QAuthorities.authorities;
        List<Authorities> items =  (List<Authorities>)authoritiesRepository
                .findAll(qAuthorities.member.eq(member));

    }
}
