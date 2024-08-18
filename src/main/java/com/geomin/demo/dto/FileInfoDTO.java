package com.geomin.demo.dto;

import lombok.*;

@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoDTO {

    private int fileId;
    private int fileSeq;
    private Long fileSize;
    private String orgFileName;
    private String saveFileName;
    private String filePath;

    private boolean img;

    public String getLink(){

        if(img){
            return "s_" + saveFileName;
        }
        else {
            return saveFileName;
        }

    }

}
