package com.geomin.demo.service;

import com.geomin.demo.dto.PatientDTO;

import java.time.LocalDate;

public class PatientUtil {

    public static PatientDTO getAgeAndGender(PatientDTO patientDTO){

        String birth = patientDTO.getIdentify();            // 주민등록번호 가져오기
        String birthYearPart = birth.substring(0, 2);       // 주민등록번호 앞 2자리 추출
        int birthYear = Integer.parseInt(birthYearPart);    // 앞 2자리 정수로 변환
        int currentYear = LocalDate.now().getYear();        // 현재 년도 가져오기
        char genderDigit = birth.charAt(7);                 // 성별 구분 번호
        int genderNumber = Character.getNumericValue(genderDigit);  // 성별 구분 번호 정수 변환

        // 나이 빈칸인 경우, 나이 자동 입력
        if(patientDTO.getAge() == 0){
            // 출생 연도 계산
            if (genderDigit == '1' || genderDigit == '2') {
                birthYear += 1900;
            } else if (genderDigit == '3' || genderDigit == '4') {
                birthYear += 2000;
            }
            patientDTO.setAge(currentYear - birthYear);
        }

        // 짝수인 경우 false(여성), 홀수인 경우 true(남성) 설정
        if(genderNumber % 2 == 0){
            patientDTO.setGender(false);
        }
        else {
            patientDTO.setGender(true);
        }

        // 설정된 patientDTO 반환
        return patientDTO;

    }


}
