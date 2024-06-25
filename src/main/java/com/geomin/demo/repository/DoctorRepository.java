package com.geomin.demo.repository;

import com.geomin.demo.domain.DoctorVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DoctorRepository {

    // user의 참조키로 의사정보 찾기
    DoctorVO findById(int referenceId);

}
