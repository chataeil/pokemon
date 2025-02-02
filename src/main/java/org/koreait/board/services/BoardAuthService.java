package org.koreait.board.services;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.koreait.board.entities.Board;
import org.koreait.board.entities.BoardData;
import org.koreait.board.entities.CommentData;
import org.koreait.board.exceptions.BoardNotFoundException;
import org.koreait.board.exceptions.GuestPasswordCheckException;
import org.koreait.board.services.comment.CommentInfoService;
import org.koreait.board.services.configs.BoardConfigInfoService;
import org.koreait.global.exceptions.scripts.AlertBackException;
import org.koreait.global.libs.Utils;
import org.koreait.member.constants.Authority;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardAuthService {
    private final Utils utils;
    private final BoardConfigInfoService configInfoService;
    private final BoardInfoService infoService;
    private final CommentInfoService commentInfoService;
    private final MemberUtil memberUtil;
    private final HttpSession session;

    /**
     * 게시판 권한 체크
     *
     * @param mode
     * @param bid
     * @param seq
     */
    public void check(String mode, String bid, Long seq) {
        System.out.printf("mode=%s, bid=%s, seq=%d%n", mode, bid, seq);
        if (!StringUtils.hasText(mode) || !StringUtils.hasText(bid) || (List.of("edit", "delete", "comment").contains(mode) && (seq == null || seq < 1L ))) {
            throw new AlertBackException(utils.getMessage("BadRequest"), HttpStatus.BAD_REQUEST);
        }

        if (memberUtil.isAdmin()) { // 관리자는 모두 허용
            return;
        }

        Board board = null;
        CommentData comment = null;
        if (mode.equals("comment")) { // 댓글 수정, 삭제
            comment = commentInfoService.get(seq); // seq를 기반으로 댓글 데이터를 가져옴
            BoardData data = comment.getData();  // 댓글이 속한 게시글 데이터
            board = data.getBoard();  // 게시글 정보를 가져옴
        } else {
            board = configInfoService.get(bid); // 게시판 정보를 가져옴
        }

        // 게시판 사용 여부 체크
        if (!board.isOpen()) {
            throw new BoardNotFoundException();
        }

        /**
         * mode - write, list  / bid
         *      - edit, view  / seq
         */
        // 글쓰기, 글 목록 권한 체크
        Authority authority = null;  // 기본값 null
        boolean isVerified = true; // 기본값 true
        Member member = memberUtil.getMember(); // 현재 로그인한 회원 정보
        if (List.of("write", "list").contains(mode)) {
            authority = mode.equals("list") ? board.getListAuthority() : board.getWriteAuthority();
        } else if (mode.equals("view")) {
            authority = board.getViewAuthority();

        } else if (List.of("edit", "delete").contains(mode)) { // 수정, 삭제
            /**
             * 1. 회원 게시글인 경우  / 직접 작성한 회원만 수정 가능
             *
             * 2. 비회원 게시글인 경우 / 비회원 비밀번호 확인이 완료된 경우 삭제 가능
             */
            BoardData item = infoService.get(seq);// 게시글 데이터 가져오기
            Member poster = item.getMember();// 게시글 작성자 정보 가져오기

            if (poster == null) { // 비회원 게시글
                /**
                 * 비회원 게시글이 인증된 경우 - 세션 키 - "board_게시글번호"가 존재
                 * 인증이 되지 않은 경우 GuestPasswordCheckException을 발생 시킨다 -> 비번 확인 절차
                 */
                if (session.getAttribute("board_" + seq) == null) { // 세션에 저장된 board_ + seq가 null값이면
                    session.setAttribute("seq", seq); // 현재 인증이 필요한 댓글 번호를 세션에 저장
                    throw new GuestPasswordCheckException();
                }

            } else if (!memberUtil.isLogin() || !poster.getEmail().equals(member.getEmail())) { // 회원 게시글  - 직접 작성한 회원만 수정 가능 통제 - 미로그인 상태 또는 로그인 상태이지만 작성자의 이메일과 일치하지 않는 경우
                isVerified = false;
            }
        } else if (mode.equals("comment")) { // 댓글 수정 삭제
            Member commenter = comment.getMember(); // 댓글단 맴버 정보 조회
            if (commenter == null) { // 비회원으로 작성한 댓글
                if (session.getAttribute("comment_" + seq) == null) { // 댓글 비회원 인증 X
                    session.setAttribute("cSeq", seq); // 인증이 필요한 비회원 번호를 세션에 저장
                    throw new GuestPasswordCheckException();
                }
            } else if (!memberUtil.isLogin() || !commenter.getEmail().equals(member.getEmail())) { // 로그인 안했거나 이메일이 사용자와 다른 경우
                isVerified = false;
            }
        }

        if ((authority == Authority.USER && !memberUtil.isLogin()) || (authority == Authority.ADMIN && !memberUtil.isAdmin())) { // 회원 권한 + 로그인 X, 관리자 권한 + 관리자 X
            isVerified = false;
        }

        if (!isVerified) { // 만약 isVerified가 false면
            throw new AlertBackException(utils.getMessage("UnAuthorized"), HttpStatus.UNAUTHORIZED); // 권한 없음 출력
        }
    }

    public void check(String mode, String bid) {
        check(mode, bid, null);
    }

    public void check(String mode, Long seq) {
        BoardData item = null; // 게시판 데이터는 기본값이 null
        if (mode.equals("comment")) {
            CommentData comment = commentInfoService.get(seq); // 댓글의 seq 받아옴
            item = comment.getData(); // 댓글에서 게시판 데이터를 받아옴
        } else {
            item = infoService.get(seq); // 인포 서비스에서 seq를 받아옴
        }

        Board board = item.getBoard(); // 게시판 정보를 받아옴
        check(mode, board.getBid(), seq); // 들어온 url 게시판의 bid 정보, seq 확인
    }
}
