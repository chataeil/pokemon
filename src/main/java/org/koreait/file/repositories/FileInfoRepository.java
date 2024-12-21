package org.koreait.file.repositories;

import org.koreait.file.entities.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long>, QuerydslPredicateExecutor<FileInfo> { //JPAReository db에 저장하기 위함 그래서 알려주는 거임.  entity 기본키가 Long crud 대신해주는역할.
// JPARepositoty는 기본적으로 findbyId save() deleteId를 제공하는데 이걸 기본키를 기준으로 값을 처리하기 때문에 기본키의 데이터 자료형인 Long을 넣음
}
