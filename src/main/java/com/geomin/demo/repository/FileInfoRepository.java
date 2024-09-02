package com.geomin.demo.repository;

import com.geomin.demo.domain.FileInfoVO;
import com.geomin.demo.dto.FileInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileInfoRepository {

    int getLastFileId();

    int insertFiles(List<FileInfoVO> uploadFiles);

    List<FileInfoVO> getFileById(int fileId);

    int deleteFile(FileInfoDTO fileInfoDTO);

    int deleteFiles(int fileId, int[] deleteFilesSeq);
}
