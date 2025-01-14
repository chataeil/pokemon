package org.koreait.board.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.global.entities.BaseEntity;
import org.koreait.member.entities.Member;

@Data
@Entity
public class CommentData extends BaseEntity {
    @Id @GeneratedValue
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY) // 한 개의 게시글에 여러 댓글인 형태
    private BoardData data;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(length = 40, nullable = false)
    private String commenter;

    @Column(length = 65)
    private String guestPw;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(length = 20)
    private String ipAddr;

    @Column(length = 150)
    private String userAgent;
}