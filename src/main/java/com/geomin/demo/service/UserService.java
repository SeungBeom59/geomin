package com.geomin.demo.service;

import com.geomin.demo.domain.UserVO;
import com.geomin.demo.dto.UserSecurityDTO;

public interface UserService {

    // 로그인
    UserVO login(String id);

    UserSecurityDTO getUser(String id);


}
