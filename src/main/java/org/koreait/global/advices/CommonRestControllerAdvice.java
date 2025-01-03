package org.koreait.global.advices;

import lombok.RequiredArgsConstructor;
import org.koreait.global.exceptions.CommonException;
import org.koreait.global.libs.Utils;
import org.koreait.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice(annotations = RestController.class) // 사용자와 JSON 형태로 주고받을 때.
@RequiredArgsConstructor
public class CommonRestControllerAdvice  {

    private final Utils utils;
    @ExceptionHandler(Exception.class)// 에러 처리
    public ResponseEntity<JSONData> errorHandler(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 기본 에러 코드 500

        Object message = e.getMessage();

        if (e instanceof CommonException commonException){
            status = commonException.getStatus();

            Map<String, List<String>> errorMessages = commonException.getErrorMessages();
            if(errorMessages != null){
                message = errorMessages;
            }else {
                message = commonException.isErrorCode() ? utils.getMessage((String)message) // 커먼 익셉션이 코드로 되어있다면 getMessage로 반환
                        : message;
            }
        }
        JSONData data = new JSONData(); // 예측 가능한 응답을 하기 위해.
        data.setSuccess(false); // 응답 실패
        data.setStatus(status); // http예외 상태 코드
        data.setMessage(message); // 예외 메세지 설정

        e.printStackTrace();

        return ResponseEntity.status(status).body(data); // 응답데이터와 JSON 데이터의 바디 데이터를 가공해서 보냄.
    }
}
