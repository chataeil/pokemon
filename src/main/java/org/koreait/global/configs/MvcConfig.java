package org.koreait.global.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableJpaAuditing // JPA 실행시키기 위해
@EnableScheduling // Schedule 실행시키기 위해
@EnableRedisHttpSession
public class MvcConfig implements WebMvcConfigurer {
    /**
     * 정적 경로 설정, CSS, JS, 이미지
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { // addResourceHandler 정적 경로 설정
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/"); // classspath 클래스 파일을 인식할 수 있는 경로
    }

    /**
     * PATCH, PUT, DELETE 등등
     * PATCH 메서드로 요청을 보내는 경우
     * <form method='POST' ...>
     *     <input type='hidden' name='_method' value='PATCH'>
     *     </form>
     * @return
     */
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter(){
        return new HiddenHttpMethodFilter();
    }// form 태그에서는 GET과 POST만 지원하기 때문에 별도의 히든 경로를 설정해서 처리할 수 있도록 함.
}
