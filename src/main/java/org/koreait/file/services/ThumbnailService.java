package org.koreait.file.services;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.koreait.file.controllers.RequestThumb;
import org.koreait.file.entities.FileInfo;
import org.koreait.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Lazy
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class ThumbnailService {

    private final FileProperties properties;
    private final FileInfoService infoService;
    private final RestTemplate restTemplate;

    /**
     * Thumbnail 경로
     * thumbs/폴더번호/seq_너비_높이.확장자
     * thumbs/폴더번호/urls/정수해시코드_너비_높이,확장자
     */

    public String create(RequestThumb form){

        Long seq = form.getSeq();
        String url = form.getUrl();
        int width = Math.max(form.getWidth(), 50);
        int height = Math.max(form.getHeight(), 50);

        String thumbPath = getThumbPath(seq, url, width, height); // 경로 만드는 기능
        File file = new File(thumbPath);
        if (file.exists()) { // 이미 thumbnail 이미지를 만든 경우
            return  thumbPath; // DB 이미 존재하는 경로면 그 경로로 출력 똑같은걸 매번 만들 수 없기 때문
        }
        try {


            if (seq != null && seq > 0L) { // 서버에 올라간 파일 seq가 있다면 썸네일 바로 만듦
                FileInfo item = infoService.get(seq);
                Thumbnails.of(item.getFilePath())
                        .size(width, height)
                        .toFile(file);

            } else if (StringUtils.hasText(url)) { // 원격 URL 이미지
                String original = String.format("%s_original", thumbPath); // 원본 이미지를 다운 받아서
                byte[] bytes = restTemplate.getForObject(URI.create(url), byte[].class); // 바이트 배열로 가져와서
                Files.write(Paths.get(original), bytes);// 파일로 저장함.

                Thumbnails.of(original) // 그 이미지를 가지고 썸네일을 하나 생성함
                        .size(width, height)
                        .toFile(file);
            } else {
                thumbPath = null;
            }
        } catch (Exception e){}

        return thumbPath;
    }
    /**
     * Thumbnail 경로
     * thumbs/폴더번호/seq_너비_높이.확장자
     * thumbs/폴더번호/urls/정수해시코드_너비_높이,확장자
     */
    public String getThumbPath(Long seq, String url, int width, int height){
        String thumbPath = properties.getPath() + "thumbs/";
        if (seq != null && seq > 0L) { // 직점 서버에 올린 파일
            FileInfo item = infoService.get(seq);

            thumbPath = thumbPath + String.format("%d/%d_%d_%d%s", seq % 10L, seq, width, height, item.getExtension()); // 서버쪽 파일이면 파일 번호가지고 만듦
        }else if (StringUtils.hasText(url)){ //  원격 URL 이미지인 경우
            String extension = url.lastIndexOf(".") == -1 ? "": url.substring(url.lastIndexOf("."));
            if (StringUtils.hasText(extension)){
                extension = extension.split("[?#]")[0];
            }
            thumbPath = thumbPath + String.format("urls/%d_%d_%d%s", Objects.hash(url), width, height, extension); // url은 해쉬코드로 만듦 해시코드는 숫자만 들어가기 때문에 url을 숫자 형태로 넣음
        }
        File file = new File(thumbPath); // 만약 세분화한 파일 경로가 없다면
        if (!file.getParentFile().exists()){

            file.getParentFile().mkdirs();// 새로 만들어줌
        }
        return thumbPath;
    }
}