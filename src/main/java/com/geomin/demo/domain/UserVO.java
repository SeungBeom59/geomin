package com.geomin.demo.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@ToString(exclude = "roleSet")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVO {

    private String id;          // 아이디
    private String password;    // 비밀번호
    private String name;        // 이름
    private UserRole roleSet;   // 권한


    public void addRole(UserRole userRole){

        this.roleSet = userRole;
    }

}
