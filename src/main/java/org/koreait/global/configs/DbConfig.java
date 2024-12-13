package org.koreait.global.configs;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class DbConfig {

    @PersistenceContext
    private EntityManager em;

    /*쿼리 dsl 기술 QueryDslPredicateExecutor 는 완전하지 않음. 복잡한 쿼리를 사용하기 위해서 직접 자세히 정의할땐 JPAQueryFactory 사용.*/
    @Lazy
    @Bean
    public JPAQueryFactory jpaQueryFactory(){
        return new JPAQueryFactory(em);
    }
}
