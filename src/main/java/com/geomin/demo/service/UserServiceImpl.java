package com.geomin.demo.service;

import com.geomin.demo.domain.DoctorVO;
import com.geomin.demo.domain.UserRole;
import com.geomin.demo.domain.UserVO;
import com.geomin.demo.dto.UserSecurityDTO;
import com.geomin.demo.repository.DoctorRepository;
import com.geomin.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public UserVO login(String id){

        log.info("userService >> login() 실행됨.");
        Optional<UserVO> userVO = userRepository.findById(id);

        UserVO result = userVO.get();

        return result;
    }

    @Override
    public UserSecurityDTO getUser(String id) {

        log.info("userService >> findById() 실행됨.");
        Optional<UserVO> userVO = userRepository.findById(id);

        if(userVO.isEmpty()){
            throw new IllegalArgumentException(id + " 인 사용자를 찾을 수 없습니다.");
        }

        UserVO result = userVO.get();


        UserSecurityDTO user = new UserSecurityDTO(result);

        if(user.getRoleSet() == UserRole.ROLE_ADMIN && result.getReferenceId() != 0){
            log.info("doctorRepository 사용");

            DoctorVO doctor = doctorRepository.findById(result.getReferenceId());

            log.info("doctor::{}",doctor);
            user.setPhone(doctor.getDoctorPhone());

            if(doctor.getDepartment() != null) {
                user.setDepartmentId(doctor.getDepartment().getDepartmentId());
                user.setDepartment(doctor.getDepartment().getDepartmentName());
                user.setDepartmentPhone(doctor.getDepartment().getDepartmentPhone());
            }

        }
        else if(user.getRoleSet() == UserRole.ROLE_USER && result.getReferenceId() != 0){
            log.info("nurseRepository 사용");
        }

        return user;
    }

}
