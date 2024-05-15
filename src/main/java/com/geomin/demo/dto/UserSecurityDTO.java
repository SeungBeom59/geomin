package com.geomin.demo.dto;

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

    private String id;          // 아이디
    private String password;    // 비밀번호
    private String name;        // 이름
    private UserRole roleSet;   // 권한

    public UserSecurityDTO(UserVO userVO){
        super(userVO.getId(), userVO.getPassword(), AuthorityUtils.createAuthorityList(userVO.getRoleSet().toString()));

        this.id = userVO.getId();
        this.password = userVO.getPassword();
        this.name = userVO.getName();
        this.roleSet = userVO.getRoleSet();
    }



//    public UserSecurityDTO(String id, String password, String name, Collection<? extends GrantedAuthority> authorities) {
//        super(id, password, authorities);
//
//        this.id = id;
//        this.password = password;
//        this.name = name;
//    }
}
