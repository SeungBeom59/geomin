package com.geomin.demo.security;

import com.geomin.demo.domain.UserVO;
import com.geomin.demo.dto.UserSecurityDTO;
import com.geomin.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

//    private PasswordEncoder passwordEncoder;
//    public CustomUserDetailsService(){
//        this.passwordEncoder = new BCryptPasswordEncoder();
//    }

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("loadUserByUsername: " + username);

        Optional<UserVO> result = userRepository.findById(username);

        if(result.isEmpty()){
            throw new UsernameNotFoundException(username + " 을 찾을 수 없습니다...");
        }

        UserVO userVO = result.get();

        UserSecurityDTO userSecurityDTO = new UserSecurityDTO(userVO);

        log.info("***************************************************************************************");
        log.info("UserSecurityDTO::{}",userSecurityDTO);
        log.info("****************************************" + userSecurityDTO.getAuthorities());
        log.info("UserSecurityDTO authorities: {}", userSecurityDTO.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toList()));

//        UserDetails userDetails = User.builder()
//                .username("user1")
//                .password(passwordEncoder.encode("1111"))
//                .authorities("ROLE_USER")
//                .build();
//
//        return userDetails;

        return userSecurityDTO;
    }
}
