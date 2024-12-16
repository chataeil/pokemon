package org.koreait.file.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.services.*;
import org.koreait.global.exceptions.BadRequestException;
import org.koreait.global.libs.Utils;
import org.koreait.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

@Tag(name="파일 API", description = "파일 업로드, 조회, 다운로드, 삭제 기능 제공합니다.") // 같은 태그끼리 엮는거 swagger
@RestController // REST API 응답을 JSON
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class ApiFileController {

    private final Utils utils;

    private final FileUploadService uploadService;

    private final FileDownloadService DownloadService;

    private final FileInfoService infoService;

    private final FileDeleteService deleteService;

    private final FileDoneService doneService;

    private final ThumbnailService thumbnailService;

    /**
     * 파일 업로드
     *
     */
    @Operation(summary = "파일 업로드 처리")
    @ApiResponse(responseCode = "201", description = "파일 업로드 처리, 업로드 성공시에는 업로드 완료된 파일 목록을 반환한다. 요청시 반드시 요청헤더에 multipart/form-data 형식으로 전송")
    @Parameters({
            @Parameter(name="gid", description = "파일 그룹 ID", required = true),
            @Parameter(name="location", description = "파일 그룹 내에서 위치 코드"),
            @Parameter(name="file", description = "업로드 파일, 복수개 전송 가능", required = true) // 요청 명세 파라미터로 작성
    })
    @ResponseStatus(HttpStatus.CREATED) //웹 브라우저 기준 요청 의도 명확하게 알기 위해서.
    @PostMapping("/upload")
    public JSONData upload(@RequestPart("file") MultipartFile[] files/*인터페아스 파일쪽은 헤더가 다름 콘텐트 타입이 다름 멀티파트 form데이터 형태로 헤더가 날라감 파트를 나눴기 때문 양식 데이터 와 파일 데이터가 형태가 다르기 때문에 동시에 전송하기 위해 파트를 나눔.*/, @Valid RequestUpload form, Errors errors) {
        if (errors.hasErrors()) { //errors 를 넣기 위해 작성된 메서드 .
            throw new BadRequestException(utils.getErrorMessages(errors)); //CommonRestController 에서 조회함.
        }

        form.setFiles(files);
        /**
         * 단일 파일 업로드
         *      - 기 업로드된 파일을 삭제하고 새로 추가
         */
        if (form.isSingle()){ // 만약 싱글파일이 맞다면. 김채원 사진 없어지고 원래 있던 파일 지운거. 싱글 파일이면 싱글톤이다.
            deleteService.deletes(form.getGid(), form.getLocation());
        }

        List<FileInfo> uploadedFiles = uploadService.upload(form); // 이 정보를 가지고 완료처리 함.

        // 업로드 완료 하자마자 완료 처리
        if (form.isDone()){
            doneService.process(form.getGid(), form.getLocation()); // 업데이트가 완료 되자마자 완료 처리 하면 돈 프로세스에 gid랑 location 추가해서 올리자마자 완료처리
        }
        JSONData data = new JSONData(uploadedFiles);
        data.setStatus(HttpStatus.CREATED); // JSON 데이터 기준 JSON 이랑 HTTP 둘다 create 로 보내겠다.
        return data;
    }

    // 파일 다운로드 중요한건 응답 헤더에 대한 통제 출력의 흐름을 파일 이름으로 바꾸기 때문에 content-disposition 있어야댐.
    @GetMapping("/download/{seq}")
    public void download(@PathVariable("seq") Long seq) // 파일 등록번호만 가지고 간단하게 구현되어 있음 주소에는 다운로드의 seq만 가지고 입력해도 파일이 다운될 수 있도록 만듦.
    { // 가변 함.
        DownloadService.process(seq);
    }

    // 파일 단일 조회
    @GetMapping("/info/{seq}")
    public JSONData info(@PathVariable("seq") Long seq) { // 등록번호 가지고 조회
        FileInfo item = infoService.get(seq);



        return new JSONData(item);
    }

    /**
     * 파일 목록 조회
     * gid, location
     */

    @GetMapping(path={"/list/{gid}", "/list/{gid}/{location}"}) // location은 필수가 아니기 때문에 잇는거 없는거 2개 넣음
    public JSONData list(@PathVariable("gid") String gid,
                         @PathVariable(name="location", required = false) String location,
                         @RequestParam(name="status", defaultValue = "DONE") FileStatus status) {

        List<FileInfo> items = infoService.getList(gid, location, status);

        return new JSONData(items);
    }

    // 파일 단일 삭제
    @DeleteMapping("/delete/{seq}")
    public JSONData delete(@PathVariable("seq") Long seq) { // {seq}를 Long 타입 seq로 쓰도록 함.

        FileInfo item = deleteService.delete(seq);

        return new JSONData(item);
    }

    @DeleteMapping({"/deletes/{gid}", "/deletes/{gid}/{location}"})
    public JSONData deletes(@PathVariable("gid") String gid, // url 경로로 부터 전달받은 gid값 String 타입으로 선언됨.
                            @PathVariable(name="location", required = false) String location) {

        List<FileInfo> items = deleteService.deletes(gid, location);
        return new JSONData(items);
    }
    @GetMapping("/thumb")
    public void thumb(RequestThumb form, HttpServletResponse response){
        String path = thumbnailService.create(form); // 핵심 기능
        if (!StringUtils.hasText(path)){ // 바로 출력할 수 있게 맞는 파일을 가져와서
            return;
        }
        File file = new File(path);
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)){

            String contentType = Files.probeContentType(file.toPath());// probeContentType 파일 경로만 보고 컨텐트 타입을 출력할 수 있는 편의기능
            response.setContentType(contentType);

            OutputStream out = response.getOutputStream(); // 바디 쪽에 출력해서 이미지를 바로 보여주는 형태.
            out.write(bis.readAllBytes());

        }catch (IOException e){

        }
    }
}