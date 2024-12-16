package org.koreait.file.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.exceptions.FileNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Lazy
@Service
@RequiredArgsConstructor
public class FileDownloadService {
    private final FileInfoService infoService;
    private final HttpServletResponse response;

    public void process(Long seq){
        FileInfo item = infoService.get(seq);

//        String filePath = item.getFilePath(); 안쓰니까 지움
        String fileName = item.getFileName();
        //윈도우에서 한글 깨짐 방지
        fileName = new String(fileName.getBytes(), StandardCharsets.ISO_8859_1); // 맥은 상관없는데 윈도우즈는 이런 문제가 발생됨.

        String contentType = item.getContentType(); // 형식을 알려줘야 하기 때문에 content-type이 다운로드에 필요함.
        contentType = StringUtils.hasText(contentType) ? contentType : "application/octet-stream"; // contenttype이 없을시 octet-stream이 기본값

        File file = new File(item.getFilePath()); // 파일 객체 만듦
        if (!file.exists()){ //누적 파일을 삭제하니까 다운로드가 안되기 때문에 예외 발생시킴.
            throw new FileNotFoundException();
        }
        try(FileInputStream fis = new FileInputStream(file); // 파일 데이터를 가져와
            BufferedInputStream bis = new BufferedInputStream(fis)){ // 버퍼에 담아서
        // 바디의 출력을 filename에 지정된 파일로 변경
        response.setHeader("Content-Disposition"/*이거 좀 중요함*/, "attachment; filename" + fileName); // 꼭 있어야 댐 Content-Disposition 이 있어야 파일을 파일 형태로 받을 수 있음 저게 있어야 다운로드 가능.
        response.setContentType(contentType);
        response.setHeader("Cache-Control", "no-cache"); // 브라우저 캐시를 사용하면 안되기 때문에 추가.
        response.setHeader("Pragma", "no-cache"); // 솔직히 없어도 됨 근데 혹시 몰라서 넣음
        response.setIntHeader("Expires", 0); // 만료시간을 없앤다. 인터넷이 느린 곳에서는 다운되다가 갑자기 안될 수 있음.
        response.setContentLengthLong(file.length());

            OutputStream out = response.getOutputStream(); // 파일 데이터 출력
            out.write(bis.readAllBytes());

        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
