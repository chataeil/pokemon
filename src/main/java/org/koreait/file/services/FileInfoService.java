package org.koreait.file.services;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.entities.QFileInfo;
import org.koreait.file.exceptions.FileNotFoundException;
import org.koreait.file.repositories.FileInfoRepository;
import org.koreait.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static org.springframework.data.domain.Sort.Order.asc;

@Lazy
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class FileInfoService  {
    private final FileInfoRepository infoRepository;

    private final FileProperties properties;

    private final HttpServletRequest request;

    public FileInfo get(Long seq) {
        FileInfo item = infoRepository.findById(seq).orElseThrow(FileNotFoundException::new);

        addInfo(item); // 추가 정보 처리 // 목록이든 상세든 처리가 동일하기 때문에 각각 처리하기 보다는 한번에 처리하고 추가하는게 강사님 스타일 열린 기능

        return item;
    }

    public List<FileInfo> getList(String gid, String location, FileStatus status) { // 목록 조회 조회가 목적 조회가 많기 때문에 불리언 빌더 사용
        status = Objects.requireNonNullElse(status, FileStatus.ALL);

        QFileInfo fileInfo = QFileInfo.fileInfo;
        BooleanBuilder andBuilder = new BooleanBuilder(); // 불리언 빌더로 조건을 더 추가할 수 있도록 사용함.
        andBuilder.and(fileInfo.gid.eq(gid)); // 필수 무조건 추가하고 시작함

        if (StringUtils.hasText(location)) { // 선택 있을때 추가하는 방식
            andBuilder.and(fileInfo.location.eq(location));
        }

        // 파일 작업 완료 상태
        if (status != FileStatus.ALL) { // ALL일때는 다 보이니까 처리할 필요가 없어서 ALL이 아닐때를 구함
            andBuilder.and(fileInfo.done.eq(status == FileStatus.DONE)); // 완료 상태일때만 true값 넣어서 완료에 대한 목록 조회를 요청했을때엔 비교해서 바로 DONE이 true일때 다른 사람이 조회가능하고 펄스면 조회불가
        }

        List<FileInfo> items = (List<FileInfo>)infoRepository.findAll(andBuilder, Sort.by(asc("createdAt"))); // 파인드 올로 조회함 파일쪽은 올린 순서대로 보여야 하기 때문에 createAt을 오름차순으로 추가함.

        // 추가 정보 처리
        items.forEach(this::addInfo); // 마지막에 추가 정보처리를 넣음 공윶

        return items;
    }

    public List<FileInfo> getList(String gid, String location) {// 보통 업로드 완료된 파일을 더 조회함. 기본값 넣음
        return getList(gid, location, FileStatus.DONE);
    }

    public List<FileInfo> getList(String gid) { // 파일 그룹작업 완료된 파일 GID로만 조회할 수 있도록 오버로딩함
        return getList(gid, null);
    }

    /**
     * 추가 정보 처리
     *
     * @param item
     */
    public void addInfo(FileInfo item) {
        // filePath - 서버에 올라간 실제 경로(다운로드, 삭제시 활용...)
        item.setFilePath(getFilePath(item));

        // fileUrl - 접근할 수 있는 주소(브라우저)
        item.setFileUrl(getFileUrl(item));
        // thumbUrl - 이미지 형식인 경우
        if (item.getContentType().contains("image/")){
            item.setThumbUrl(String.format("%s/api/file/thumb?seq=%d", request.getContextPath(), item.getSeq()));
        }
    }

    public String getFilePath(FileInfo item) {
        Long seq = item.getSeq();
        String extension = Objects.requireNonNullElse(item.getExtension(), "");
        return String.format("%s%s/%s", properties.getPath(), getFolder(seq), seq + extension);
    }

    public String getFilePath(Long seq) {
        FileInfo item = infoRepository.findById(seq).orElseThrow(FileNotFoundException::new);
        return getFilePath(item);
    }

    public String getFileUrl(FileInfo item) {
        Long seq = item.getSeq();
        String extension = Objects.requireNonNullElse(item.getExtension(), "");
        return String.format("%s%s%s/%s", request.getContextPath(), properties.getUrl(), getFolder(seq), seq + extension);
    }

    public String getFileUrl(Long seq) {
        FileInfo item = infoRepository.findById(seq).orElseThrow(FileNotFoundException::new);
        return getFileUrl(item);
    }

    private long getFolder(long seq) {
        return seq % 10L;
    }
}