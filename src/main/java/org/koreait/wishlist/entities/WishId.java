package org.koreait.wishlist.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.koreait.member.entities.Member;
import org.koreait.wishlist.constants.WishType;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class WishId { // 멤버만의 고유 seq와 타입으로 찜 데이터 관리를 위해
    private Long seq;
    private WishType type;
    private Member member;
}
