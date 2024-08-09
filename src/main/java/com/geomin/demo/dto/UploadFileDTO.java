package com.geomin.demo.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileDTO {

    private List<MultipartFile> files;

}
