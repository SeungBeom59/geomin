package com.geomin.demo.repository;

import com.geomin.demo.domain.UserRole;
import com.geomin.demo.domain.UserVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertUser(){

        UserVO user = UserVO.builder()
                .id("user1")
                .password(passwordEncoder.encode("1234"))
                .name("이현승")
                .referenceId(2)
                .build();

        user.addRole(UserRole.ROLE_ADMIN);

        System.out.println("*******************************");

        userRepository.save(user);

    }

}
