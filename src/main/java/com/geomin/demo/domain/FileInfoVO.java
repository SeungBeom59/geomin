package com.geomin.demo.domain;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class FileInfoVO {

    private int fileId;
    private int fileSeq;
    private String orgFileName;
    private String saveFileName;
    private String filePath;
    private long fileSize;




}
