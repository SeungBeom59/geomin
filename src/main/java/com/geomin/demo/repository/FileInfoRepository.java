package com.geomin.demo.repository;

import com.geomin.demo.domain.FileInfoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileInfoRepository {

    int getLastFileId();

    void insertFiles(List<FileInfoVO> uploadFiles);




}
