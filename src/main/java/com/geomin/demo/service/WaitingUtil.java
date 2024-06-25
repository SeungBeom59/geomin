package com.geomin.demo.service;

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
