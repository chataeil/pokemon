package org.koreait.file.services;

import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.repositories.FileInfoRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class FileDoneService {

    private final FileInfoService infoService;
    private final FileInfoRepository repository;

    public void process(String gid, String location){
        List<FileInfo> items = infoService.getList(gid, location, FileStatus.ALL); // FileStatus 제대로 올라간거 제대로 올라가지 않은거.

        items.forEach(item -> item.setDone(true)); // 업로드가 성공했으면.

        repository.saveAllAndFlush(items); // 저장하고 커밋.
    }
    public void process(String gid){
        process(gid, null);
    } // 매개변수가 gid 하나일 경우.
}