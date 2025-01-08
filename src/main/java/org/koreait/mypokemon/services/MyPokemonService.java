package org.koreait.mypokemon.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.koreait.member.repositories.MemberRepository;
import org.koreait.mypokemon.entities.MyPokemon;
import org.koreait.mypokemon.entities.MyPokemonId;
import org.koreait.mypokemon.repositories.MyPokemonRepository;
import org.koreait.wishlist.constants.WishType;
import org.koreait.mypokemon.entities.QMyPokemon; // QMyPokemon 추가
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
@Transactional
public class MyPokemonService {
    private final MemberUtil memberUtil;
    private final MyPokemonRepository repository;
    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;
    private final SpringTemplateEngine templateEngine;

    public void process(String mode, Long seq) {
        if (!memberUtil.isLogin()) {
            return;
        }

        mode = StringUtils.hasText(mode) ? mode : "add";
        Member member = memberUtil.getMember();
        member = memberRepository.findByEmail(member.getEmail()).orElse(null);
        try {
            if (mode.equals("remove")) {
                MyPokemonId pokemonId = new MyPokemonId(seq, member);
                repository.deleteById(pokemonId);

            } else { // 포켓몬 추가
                MyPokemon pokemon = new MyPokemon();
                pokemon.setSeq(seq);
                pokemon.setMember(member);
                repository.save(pokemon);
            }

            repository.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Long> getMyPokemon() {
        if (!memberUtil.isLogin()) { // 로그인 여부 확인
            return List.of(); // 아닐시 빈 리스트 반환
        }

        QMyPokemon qMyPokemon = QMyPokemon.myPokemon; // Q객체 생성
        BooleanBuilder builder = new BooleanBuilder(); // BooleanBuilder 생성
        builder.and(qMyPokemon.member.eq(memberUtil.getMember()));

        List<Long> items = queryFactory.select(qMyPokemon.seq)
                .from(qMyPokemon)
                .where(builder)
                .fetch();

        return items;
    }

    public String showPokemon(Long seq) {
        return showPokemon(seq, null);
    }

    public String showPokemon(Long seq, List<Long> myPokemons) {
        myPokemons = myPokemons == null || myPokemons.isEmpty() ? getMyPokemon() : myPokemons;

        Context context = new Context();
        context.setVariable("seq", seq);
        context.setVariable("myPokemons", myPokemons);
        context.setVariable("isMine", myPokemons.contains(seq));
        context.setVariable("isLogin", memberUtil.isLogin());

        return templateEngine.process("common/_my_pokemon_list", context);  // 템플릿 처리
    }
}