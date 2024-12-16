package org.koreait.global.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class I18NConfig implements WebMvcConfigurer { // internationalization 의 약자


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
//                .addPathPatterns("/**") - 사이트 전역에 적용하는 패턴일 경우 생략 가능

    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor(){ // 쿠키 설정 안해서 오류남
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("language"); //http://localhost:3000/member/login?language=jp

        return interceptor;
    }
    @Bean
    public CookieLocaleResolver localeResolver(){ // 그래서 해결해줌
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieMaxAge(60 * 60);
        resolver.setCookieName("language");
        return resolver;
    }
}
