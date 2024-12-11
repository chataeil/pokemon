package org.koreait.member.repositories;

import org.koreait.member.entities.Authorities;
import org.koreait.member.entities.AuthoritiesID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

// AutoritiesId를 입력해야 됨 복합키기 떄문.
public interface AuthoritiesRepository extends JpaRepository<Authorities, AuthoritiesID>, QuerydslPredicateExecutor<Authorities> {
}
