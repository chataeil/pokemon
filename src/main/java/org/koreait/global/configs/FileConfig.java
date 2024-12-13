package org.koreait.global.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class) // 의존성 주입 방식.
public class FileConfig implements WebMvcConfigurer {

    // 정적 경로 설정. Value 를 통해서 가져올 수 있지만 yml 설정값들을 범주화 해서 관리하는게 더 편하기 때문에 data class 형태로 분리함.
    private final FileProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { // spring web mvc 설정이기 때문에 WebMvcConfigurer 인터페이스 사용.
        registry.addResourceHandler(properties.getUrl()+ "**")
                .addResourceLocations("file:///" + properties.getPath()); //업로드 경로를 연결 폴더와 브라우저에서  연결 관리 가능하게.
    }
}
