package com.geomin.demo.controller;

import com.geomin.demo.dto.UploadFileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UpDownController {

    @Value("${com.geomin.upload.path}")
    private String uploadPath;

    @PostMapping(value = "/upload" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(UploadFileDTO uploadFileDTO){

        log.info("post >> /upload... upload() 실행.");

        log.info("uploadFileDTO::{}",uploadFileDTO);

//        return null;
    }

}
