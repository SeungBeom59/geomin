package com.geomin.demo.security;

import com.geomin.demo.domain.DoctorVO;
import com.geomin.demo.domain.UserRole;
import com.geomin.demo.domain.UserVO;
import com.geomin.demo.dto.UserSecurityDTO;
import com.geomin.demo.repository.DoctorRepository;
import com.geomin.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
    private final DoctorRepository doctorRepository;


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
