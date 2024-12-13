package org.koreait.global.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "file.upload") // 범주화 경로.
public class FileProperties {
    private String path;
    private String url;

}
