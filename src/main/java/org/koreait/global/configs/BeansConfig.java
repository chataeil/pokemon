package org.koreait.global.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;
// 수동 등록 빈 관리
@Configuration
public class BeansConfig {

    @Lazy // 지연 로딩
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    /**
     * 항상 사용하는게 아닌 필요할 때만 사용
     *
     * 범용 기능이면 거의 대부분 class class 를 사용함.
     * @return
     */
    @Lazy
    @Bean
    public ModelMapper modelMapper(){ // 커맨드 객체 값 자동으로 get, set 알아서 체크 해줌. 사용시 class class 필수로 들어감. 범용 기능.
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return mapper;
    }

    @Lazy
    @Bean
    public ObjectMapper objectMapper() { // RestController에 정의되어 있음 Rest컨트롤러는 제이슨 형태 Request바디에 정보 입력하면 제이슨 형태로 바꿔주는 기능.
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule()); // java8 data & time api - java.time 패키지

        return om;
    }
}
