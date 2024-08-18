package com.geomin.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileDTO {

    private List<MultipartFile> uploadFiles;

}
