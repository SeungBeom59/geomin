package com.geomin.demo.service;

import com.geomin.demo.domain.FileInfoVO;
import com.geomin.demo.dto.FileInfoDTO;
import com.geomin.demo.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {


    private final FileInfoRepository fileInfoRepository;

    @Value("${com.geomin.upload.path}")
    private String uploadPath;

    // 파일 업로드
    @Transactional
    public int upload(List<MultipartFile> uploadFiles){

        log.info("===================================================================");
        log.info("fileService... upload 처리 진행");

        List<FileInfoVO> fileList = new ArrayList<>();
        int lastFileId = fileInfoRepository.getLastFileId();

        AtomicInteger fileSeq = new AtomicInteger(1);

        uploadFiles.forEach(file -> {
            log.info(file.getOriginalFilename());


            String originalName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String saveFileName = uuid + "_" + originalName;
            boolean img = false;

            Path savePath = Paths.get(uploadPath, saveFileName);

            // 파일 저장 시도
            try {
                file.transferTo(savePath);

                // 이미지 파일일 경우, 썸네일 생성
                if(Files.probeContentType(savePath).startsWith("image")){
                    img = true;
                    File thumbFile = new File(uploadPath, "S_" + saveFileName);
                    Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                }
            }
            catch (IOException e) {
                log.error("Failed to save file: " + originalName, e);
                throw new RuntimeException("File upload failed", e);
            }

            FileInfoVO vo = FileInfoVO.builder()
                    .fileId(lastFileId + 1)
                    .fileSeq(fileSeq.getAndIncrement())
                    .orgFileName(file.getOriginalFilename())
                    .saveFileName(saveFileName)
                    .filePath(savePath.toString())
                    .fileSize(file.getSize())
                    .img(img)
                    .build();

            fileList.add(vo);

        });

        fileInfoRepository.insertFiles(fileList);

        log.info("fileList::{}" , fileList);
        log.info("fileService... upload 처리 완료");

        return lastFileId + 1;

    }

    // 기존 파일항목에 업로드
    public int uploadAdditionalFiles(List<MultipartFile> uploadFiles,
                                      int fileInfoId) {

        log.info("===================================================================");
        log.info("fileService... uploadAdditionalFiles 처리 진행");

        // 해당 번호로 파일 리스트 가져오기
        List<FileInfoVO> resultFile =  fileInfoRepository.getFileById(fileInfoId);

        int lastFileId = 0;
        int lastFileSeq = 0;

        // 파일 리스트가 존재한다면 해당 파일들에서 마지막 seq를 가져오기
        if(resultFile != null && resultFile.size() > 0){
            int fileSeq = 0;

            for(int i = 0; i < resultFile.size(); i++){

                int seq = resultFile.get(i).getFileSeq();
                fileSeq = Math.max(fileSeq , seq);
            }

            lastFileId = fileInfoId;
            lastFileSeq = fileSeq + 1;

        }
        else {
            // 만약 null이라면 기존의 파일 ID 사용은 하지만,
            // seq의 증가는 1부터 하도록
            lastFileId = fileInfoId;
            lastFileSeq = 1;
        }

        int lastFinalFileId = lastFileId;

        List<FileInfoVO> fileList = new ArrayList<>();
        AtomicInteger fileSeq = new AtomicInteger(lastFileSeq);

        uploadFiles.forEach(file -> {
            log.info(file.getOriginalFilename());


            String originalName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String saveFileName = uuid + "_" + originalName;
            boolean img = false;

            Path savePath = Paths.get(uploadPath, saveFileName);

            // 파일 저장 시도
            try {
                file.transferTo(savePath);

                // 이미지 파일일 경우, 썸네일 생성
                if(Files.probeContentType(savePath).startsWith("image")){
                    img = true;
                    File thumbFile = new File(uploadPath, "S_" + saveFileName);
                    Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                }
            }
            catch (IOException e) {
                log.error("Failed to save file: " + originalName, e);
                throw new RuntimeException("File upload failed", e);
            }

            FileInfoVO vo = FileInfoVO.builder()
                    .fileId(lastFinalFileId)
                    .fileSeq(fileSeq.getAndIncrement())
                    .orgFileName(file.getOriginalFilename())
                    .saveFileName(saveFileName)
                    .filePath(savePath.toString())
                    .fileSize(file.getSize())
                    .img(img)
                    .build();

            fileList.add(vo);

        });

        fileInfoRepository.insertFiles(fileList);

        log.info("fileService...uploadAdditionalFiles 처리 완료");

        return lastFinalFileId;

    }


    public List<FileInfoDTO> getFileById(int fileId) {

        List<FileInfoVO> fileList = fileInfoRepository.getFileById(fileId);

        List<FileInfoDTO> fileDTOList = new ArrayList<>();

        fileList.forEach( vo -> {

            FileInfoDTO dto = FileInfoDTO.builder()
                    .fileId(vo.getFileId())
                    .fileSeq(vo.getFileSeq())
                    .orgFileName(vo.getOrgFileName())
                    .saveFileName(vo.getSaveFileName())
                    .filePath(vo.getFilePath())
                    .fileSize(vo.getFileSize())
                    .img(vo.isImg())
                    .build();

            fileDTOList.add(dto);
        });

        return fileDTOList;
    }

    // 파일 미리보기
    public ResponseEntity<Resource> viewFile(String fileName){

        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        HttpHeaders headers = new HttpHeaders();

        if(!resource.exists()){
            return ResponseEntity.notFound().build();
        }

        try{
            headers.add("Content-Type" , Files.probeContentType(resource.getFile().toPath()));
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    // 파일 다운로드
    public ResponseEntity<?> downloadFile(String fileName) {

        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        HttpHeaders headers = new HttpHeaders();

        if(!resource.exists()){
            return ResponseEntity.notFound().build();
        }

        try {
            String orgFileName = fileName.substring(fileName.indexOf('_') + 1);
            String encodedFileName = URLEncoder.encode(orgFileName , StandardCharsets.UTF_8).replaceAll("\\+","%20");
            headers.add("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    // 파일 삭제 (db에서만 삭제, 실제 파일 삭제 X)
    public boolean deleteFile(FileInfoDTO fileInfoDTO) {

        int result = fileInfoRepository.deleteFile(fileInfoDTO);

        if(result == 1){
            return true;
        }
        else{
            return false;
        }

    }

    // 파일 일괄 삭제(db에서만 삭제, 실제 파일 삭제 X)
    public boolean deleteFiles(int fileId, List<Integer> deleteFiles){

        int[] deleteFilesSeq = deleteFiles.stream().mapToInt(i -> i).toArray();

        int result = fileInfoRepository.deleteFiles(fileId , deleteFilesSeq);

        return result == deleteFilesSeq.length;

    }


}
