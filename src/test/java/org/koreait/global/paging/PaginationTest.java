package org.koreait.global.paging;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles({"default", "test"})
public class PaginationTest {
    @Mock // 가짜 객체 생성
    private HttpServletRequest request; // 서버가 돌아가야지 객체 생성하는 인터페이스

    @BeforeEach
    void init() {
        // Stub(스텁 - 가짜 데이터)
        given(request.getQueryString()).willReturn("query=블로그&test1=1=1&test2=2&page=3");
//        given(request.getQueryString()).willReturn(null);
    }

    @Test
    void test1(){
        //(int page, int total, int ranges, int limit)
        Pagination pagination = new Pagination(23, 9999, 10, 20, request);
        System.out.println(pagination);

        pagination.getPages().forEach(s -> System.out.println(Arrays.toString(s)));
    }
}