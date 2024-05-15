package com.geomin.demo.repository;

import com.geomin.demo.domain.UserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserRepository {

    /**
     * 사용자 로그인 정보 확인
     * @param id
     * @return UserVO
     */
    Optional<UserVO> findById(String id);

    /**
     *
     * @param vo
     * @return int
     */
    int save(UserVO vo);

}
