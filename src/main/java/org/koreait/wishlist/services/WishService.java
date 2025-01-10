package org.koreait.wishlist.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.koreait.global.exceptions.BadRequestException;
import org.koreait.global.libs.Utils;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.koreait.member.repositories.MemberRepository;
import org.koreait.wishlist.constants.WishType;
import org.koreait.wishlist.entities.QWish;
import org.koreait.wishlist.entities.Wish;
import org.koreait.wishlist.entities.WishId;
import org.koreait.wishlist.repositories.WishRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
@Transactional
public class WishService {
    private final MemberUtil memberUtil;
    private final WishRepository repository;
    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;
    private final SpringTemplateEngine templateEngine;
    private final Utils utils;

    public void process(String mode, Long seq, WishType type) { // 찜등록하는 역할
        if (!memberUtil.isLogin()) {
            return;
        }

        mode = StringUtils.hasText(mode) ? mode : "add";
        Member member = memberUtil.getMember(); // 로그인 상태 확인
        member = memberRepository.findByEmail(member.getEmail()).orElse(null); // 레포지토리에서 멤버 이메일 조회
        try {
            if (mode.equals("remove")) { // mode가 remove면
                WishId wishId = new WishId(seq, type, member); //생성해서 삭제할 대상을 정확히 명시
                repository.deleteById(wishId);// 삭제

            } else { // 찜 추가 여기서 통제
                // 게임용 포켓몬 선택 제한 (6개)
                if (type == WishType.GAME_POKEMON){// 새로 추가
                    QWish wish = QWish.wish; //Query dsl
                    BooleanBuilder builder = new BooleanBuilder(); // 쿼리 조건 실행하는 불리언 객체 생성
                    builder.and(wish.member.eq(member))  // 식별한 로그인 멤버 확인
                            .and(wish.type.eq(WishType.GAME_POKEMON));// 멤버가 선택한 타입 확인
                    long total = repository.count(builder); // 만들어진 빌더를 레포지토리에 카운트
                    if (total >= 6L){ // 카운트 해서 6개 이상은 선택 불가
                        throw new BadRequestException(utils.getMessage("MaxChoice.gamePokemon"));
                    }
                }
                Wish wish = new Wish();
                wish.setSeq(seq);
                wish.setType(type);
                wish.setMember(member);
                repository.save(wish); // 저장
            }

            repository.flush();
        } catch (Exception e) {
            if (e instanceof BadRequestException){
                throw e;
            }

            e.printStackTrace();
        }
    }

    public List<Long> getMyWish(WishType type) {
        if (!memberUtil.isLogin()) {
            return List.of();
        }

        QWish wish = QWish.wish; // 쿼리 dsl
        BooleanBuilder builder = new BooleanBuilder();//쿼리 조건 생성하는 빌더 객체
        builder.and(wish.member.eq(memberUtil.getMember())) // 로그인한 사용자와 일치
                .and(wish.type.eq(type)); // 찜 항목의 타입

        List<Long> items = queryFactory.select(wish.seq) // 찜한 항목의 고유 ID
                .from(wish) // 쿼리 대상
                .where(builder) // booleanBuilder 객체에 추가된 조건들을 WHERE 절에 적용
                .fetch(); //쿼리를 실행하여 결과

        return items;

    }

    public String showWish(Long seq, String type) {
        return showWish(seq, type, null);
    }

    public String showWish(Long seq, String type, List<Long> myWishes) {
        WishType _type = WishType.valueOf(type);
        myWishes = myWishes == null || myWishes.isEmpty() ? getMyWish(_type) : myWishes;

        Context context = new Context(); // 템플릿에 전달할 데이터 저장
        context.setVariable("seq", seq);
        context.setVariable("type", _type);
        context.setVariable("myWishes", myWishes);
        context.setVariable("isMine", myWishes.contains(seq));
        context.setVariable("isLogin", memberUtil.isLogin());
        // 전부 템플릿에 전달
        return templateEngine.process("common/_wish", context); //common/_wish 템플릿 파일 처리
    }
}
