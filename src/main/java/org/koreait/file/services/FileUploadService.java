package org.koreait.file.services;

import lombok.RequiredArgsConstructor;
import org.koreait.file.controllers.RequestUpload;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.repositories.FileInfoRepository;
import org.koreait.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Lazy
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class FileUploadService {
    private final FileProperties properties; // 업로드할 파일 경로가 필요하기 때문
    private final FileInfoRepository fileInfoRepository;
    private final FileInfoService infoService;
    public List<FileInfo> upload(RequestUpload form) {
        String gid = form.getGid(); // 필수라 검증
        gid = StringUtils.hasText(gid) ? gid : UUID.randomUUID().toString(); // gid는 필수기 때문에 만약 gid가 없다면 자바쪽에서 유니크 아이디를 만들 수 있는 편의기능인 UUID 사용

        String location = form.getLocation(); // 필수는 아니지만 있으면 표시
        MultipartFile[] files = form.getFiles(); // 실제로 올라와 있는 파일 정보.

        String rootPath = properties.getPath(); // 업로드할 파일에 대한 경로가 필요 // 업로드할 파일 경로가 필요하기 때문


        // 파일 업로드 성공 파일 정보
        List<FileInfo> uploadedItems = new ArrayList<>(); // 만약 파일을 올리는데 실패할 수 있기에 성공한 파일만 올리는 메서드 업로드가 된 파일의 정보를 JSON 데이터로 모아서 후속처리

        for (MultipartFile file : files) { 
            String contentType = file.getContentType();
            // 이미지 형식의 파일만 허용하는 경우 - 이미지가 아닌 파일은 건너띄기
            if (form.isImageOnly() && contentType.indexOf("image/") == -1) { //이미지가 아닌걸 거르기 위해 작성.
                continue;
            }

            // 1. 파일 업로드 정보 - DB에 기록 S
            // 파일명.확장자 // model.weights.h5
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf("."));// 마지막에 있는 점에서 끝까지가 확장자.


            FileInfo item = new FileInfo(); // 확장자명 DB에 세팅해서 기록.
            item.setGid(gid);
            item.setLocation(location);
            item.setFileName(fileName);
            item.setExtension(extension);
            item.setContentType(contentType);

            fileInfoRepository.saveAndFlush(item);

            // 1. 파일 업로드 정보 - DB에 기록 E

            // 2. 파일 업로드 처리 S
            long seq = item.getSeq(); //파일 등록번호로 만들기 때문에
            String uploadFileName = seq + extension;
            long folder = seq % 10L; // 0 ~ 9 // 파일을 10개로 나눔
            File dir = new File(rootPath + folder);
            // 디렉토리가 존재 하지 않거나 파일로만 있는 경우 생성
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs(); //파일이 없다면 만들어줌
            }

            File _file = new File(dir, uploadFileName);
            try {
                file.transferTo(_file); //파일을 업로드 성공 // 만약 오류가 발생하면 파일의 업로드가 실패한 것.

                // 추가 정보 처리
                infoService.addInfo(item);

                uploadedItems.add(item);

            } catch (IOException e) {
                // 파일 업로드 실패 -> DB 저장된 데이터를 삭제
                fileInfoRepository.delete(item); // 파일 정보는 회원 정보기 때문에 파일 업로드 실패하면 파일 삭제
                fileInfoRepository.flush();
            }
            // 2. 파일 업로드 처리 E
        }


        return uploadedItems;
    }
}