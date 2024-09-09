package com.geomin.demo.repository;

import com.geomin.demo.domain.KcdVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KcdRepository {


    List<KcdVO> getKcdListById(int kcdId);

    int getLastKcdId();

    int insertKcds(List<KcdVO> kcdVOs);

    void deleteById(int kcdId);
}
