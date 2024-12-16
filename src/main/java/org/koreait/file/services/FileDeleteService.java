package org.koreait.file.services;

import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.repositories.FileInfoRepository;
import org.koreait.global.exceptions.UnAuthorizedException;
import org.koreait.member.libs.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class FileDeleteService {
    private final FileInfoService infoService;
    private final FileInfoRepository infoRepository;
    private final MemberUtil memberUtil;

    public FileInfo delete(Long seq){
        FileInfo item = infoService.get(seq); // 파일 seq 조회
        String filePath = item.getFilePath(); // 파일 경로 조회
        // 0. 파일 소유자만 삭제 가능하게 통제 - 다만 관리자는 가능
        String createdBy = item.getCreatedBy();
        if (!memberUtil.isAdmin() && StringUtils.hasText(createdBy)
                && (!memberUtil.isLogin() || !memberUtil.getMember().getEmail().equals(createdBy))){
            throw new UnAuthorizedException();
        }

        // 1. DB에서 정보를 제거
        infoRepository.delete(item);
        infoRepository.flush();
        
        // 2. 파일이 서버에 존재하면 파일도 삭제한다.
        File file = new File(filePath);
        if (file.exists() && file.isFile()){ // 파일 삭제 후에 나온 제거 데이터도 같이 반환값으로 내보낼 필요가 있음 APIfilecontroller에서 반환값을 같이 출력해주는 방향으로 개발됨.
            file.delete();
        }

        // 3. 삭제된 파일 정보를 반환 열린 기능
        return item;
    }

    public List<FileInfo> deletes(String gid, String location){ // gid랑 location을 가지고
        List<FileInfo> items = infoService.getList(gid, location, FileStatus.ALL); // 전체 조회한 다음
        items.forEach(i -> delete(i.getSeq())); // 이걸 일괄 삭제함.

        return items;
    }

    public List<FileInfo> deletes(String gid){
        return deletes(gid, null);
    } // gid만 가지고도 지울 수 있게 오버로딩 게시판에는 파일이 많기 때문에 GID하나만 가지고 일괄 삭제할 예정임.
}
