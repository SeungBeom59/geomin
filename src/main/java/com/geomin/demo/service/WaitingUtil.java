package com.geomin.demo.service;

import com.geomin.demo.domain.WaitingVO;
import com.geomin.demo.dto.WaitingDTO;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public class WaitingUtil {

    public static String getWaitingType(int waitingType){

        String type = "";

        if(waitingType == 0){
            type = "일반진료";
        }
        else if(waitingType == 1){
            type = "전문진료";
        }
        else if(waitingType == 2){
            type = "입원진료";
        }
        else if(waitingType == 3){
            type = "응급진료";
        }
        else if(waitingType == 4){
            type = "외래진료";
        }
        else if(waitingType == 5){
            type = "재활진료";
        }
        else {
            type = "알수없음";
        }

        return type;

    }

    public static String getWaitingStatus(int waitingStatus) {

        String status = "";

        if(waitingStatus == 0){
            status = "대기중";
        }
        else if(waitingStatus == 1){
            status = "진료중";
        }
        else if(waitingStatus == 2){
            status = "진료완료";
        }
        else if(waitingStatus == 3){
            status = "검사중";
        }
        else if(waitingStatus == 4){
            status = "검사대기중";
        }
        else {
            status = "알수없음";
        }

        return status;

    }

    public static WaitingVO modifyWaitingStatusOrType(WaitingDTO waitingDTO){

        String value = waitingDTO.getAction();

        int waitingValue = -1;
        boolean statusOrType = false;

        String[] actionList = {"대기중" , "진료중" , "진료완료" , "검사중" , "검사대기"};

        for (String s : actionList) {
            if(waitingDTO.getAction().equals(s)){
                statusOrType = true;
                break;
            }
        }

        if(value.equals("대기중") || value.equals("일반진료")){
            waitingValue = 0;
        }
        else if(value.equals("진료중") || value.equals("전문진료")){
            waitingValue = 1;
        }
        else if(value.equals("진료완료") || value.equals("입원진료")){
            waitingValue = 2;
        }
        else if(value.equals("검사중") || value.equals("응급진료")){
            waitingValue = 3;
        }
        else if(value.equals("검사대기") || value.equals("외래진료")){
            waitingValue = 4;
        }
        else if(value.equals("재활진료")){
            waitingValue = 5;
        }


        // true 라면 Status 이고 false 라면 type 에 대한 설정.
        if(statusOrType){
            WaitingVO vo =  WaitingVO.builder()
                    .waitingId(waitingDTO.getWaitingId())
                    .waitingStatus(waitingValue)
                    .waitingType(-1)
                    .build();

            return vo;
        }
        else {
            WaitingVO vo =  WaitingVO.builder()
                    .waitingId(waitingDTO.getWaitingId())
                    .waitingType(waitingValue)
                    .waitingStatus(-1)
                    .build();

            return vo;
        }

    }

    public static String getIdentify(String identify){

        String result = identify.substring(0,2) + "-"
                      + identify.substring(2,4) + "-"
                      + identify.substring(4,6);

        char genderDigit =  identify.charAt(7);

        if(genderDigit == '1' || genderDigit == '2' || genderDigit == '5' || genderDigit == '6'){
            result = "19" + result;
        }
        else if(genderDigit == '3' || genderDigit == '4' || genderDigit == '7' || genderDigit == '8'){
            result = "20" + result;
        }

        return result;
    }


    public static String createWaitingKey(String lastWaitingKey) {

        String newKey = "";
        int newNumber = 0;

        LocalDate today = LocalDate.now();
        log.info("today = " +  today);

        String frontKey = today.toString()
                                .replaceAll("-" , "")
                                .substring(2);
        log.info("frontKey = " + frontKey);

        if(lastWaitingKey.isEmpty() || lastWaitingKey.equals("0")){
            newNumber = 1;
        }
        else {
            int oldNumber = Integer.parseInt(lastWaitingKey.substring(7));
            newNumber = oldNumber + 1;
        }

        newKey = frontKey + "T" + newNumber;
        log.info("New Waiting Key Created : " + newKey);

        return newKey;
    }
}
