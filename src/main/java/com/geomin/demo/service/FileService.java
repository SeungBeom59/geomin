package com.geomin.demo.service;

import com.geomin.demo.domain.FileInfoVO;
import com.geomin.demo.dto.FileInfoDTO;
import com.geomin.demo.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
}
