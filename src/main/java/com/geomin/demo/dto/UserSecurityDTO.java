package com.geomin.demo.dto;

import com.geomin.demo.domain.DoctorVO;
import com.geomin.demo.domain.UserRole;
import com.geomin.demo.domain.UserVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@Setter
public class UserSecurityDTO extends User {

    private String id;              // 아이디
    private String password;        // 비밀번호
    private String name;            // 이름
    private UserRole roleSet;       // 권한
    private int departmentId;       // 소속 번호
    private String department;      // 소속
    private String departmentPhone; // 소속 연락처
    private String phone;           // 개인 연락처
    private int referenceId;        // 직급 참조키

    public UserSecurityDTO(UserVO userVO){
        super(userVO.getId(), userVO.getPassword(), AuthorityUtils.createAuthorityList(userVO.getRoleSet().toString()));

        this.id = userVO.getId();
        this.password = userVO.getPassword();
        this.name = userVO.getName();
        this.roleSet = userVO.getRoleSet();
        this.referenceId = userVO.getReferenceId();
    }

}
