package com.geomin.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geomin.demo.dto.ApiResponseDTO;
import com.geomin.demo.dto.MedicineDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

// 공공데이터
// 식품의약품안전처 : e약은요 api
@Controller
@Slf4j
public class ApiExplorer {

    @Value("${apis.data.key}")
    private String key;

    @GetMapping("/medicine-prescription")
    public String getMedicinePopup( @RequestParam(name = "type" , required = false) String type,
                                    @RequestParam(name = "keyword" , required = false) String keyword,
                                    @RequestParam(name = "page" , defaultValue = "1") int page,
                                    Model model ) throws IOException {

        log.info("get >> /medicine-prescription... getMedicinePopup() 실행.");

        ApiResponseDTO apiResult = getAPi(type , keyword , page);
        List<MedicineDTO> medicineDTOList   = apiResult.getBody().getItems();

        model.addAttribute("medicineDTOList", medicineDTOList);
        model.addAttribute("apiResult", apiResult);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);

        return "medicine_prescription_popup";
    }


    public ApiResponseDTO getAPi(String type , String keyword , int page) throws IOException {

        int pageSize = 5;   // 한페이지에서 보여주려는 알약의 갯수

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + key); /*Service Key*/

        if(type != null && keyword != null){
            if(type.equals("itemName")){
                urlBuilder.append("&" + URLEncoder.encode("itemName","UTF-8") + "=" + URLEncoder.encode(keyword, "UTF-8")); /*제품명*/
            }
            else if(type.equals("entpName")){
                urlBuilder.append("&" + URLEncoder.encode("entpName","UTF-8") + "=" + URLEncoder.encode(keyword, "UTF-8")); /*업체명*/
            }
            else if(type.equals("itemSeq")){
                urlBuilder.append("&" + URLEncoder.encode("itemSeq","UTF-8") + "=" + URLEncoder.encode(keyword, "UTF-8")); /*품목기준코드*/
            }
        }

        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("" + page , "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("" + pageSize, "UTF-8")); /*한 페이지 결과 수*/


        urlBuilder.append("&" + URLEncoder.encode("type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*응답데이터 형식(xml/json) Default:xml*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
//        System.out.println(sb.toString());
        System.out.println("******************************************************************************");

        // json 형태 StringBuilder to DTO
        // objMapper를 이용하여 header와 body로 구성된 responseDTO로 매핑시킨다.
        ObjectMapper objectMapper = new ObjectMapper();
        ApiResponseDTO responseDTO = objectMapper.readValue(sb.toString(), ApiResponseDTO.class);

        // 페이징
        int pageNo = responseDTO.getBody().getPageNo();
        int totalCount = responseDTO.getBody().getTotalCount();
        int numOfRows = responseDTO.getBody().getNumOfRows();
        int totalPages = (int) Math.ceil((double)totalCount / numOfRows );

        // 전체페이지 = 총 갯수 / 보여주는 갯수 인데, 실수로 받아서 올림처리.
        // 정수로는 26 / 5 =  5.2 가량으로 남은 페이지가 있는 것이므로 올림 처리하여 6페이지가 되도록 처리.
        int currentGroup = (int)Math.ceil((double) pageNo / pageSize);         // 현재 페이지가 속하는 페이지 그룹

        int prevGroupStartPage = (currentGroup - 2) * pageSize + 1;
        prevGroupStartPage = Math.max(prevGroupStartPage, 1);       // 1 보다는 작아지지 않도록

        int currentGroupStartPage = (currentGroup - 1) * pageSize + 1;
        int currentGroupEndPage = Math.min(currentGroup * pageSize, totalPages);

        int nextGroupStartPage = currentGroup * pageSize + 1;
        nextGroupStartPage = Math.min(nextGroupStartPage, totalPages);  // 전체 페이지 보다는 많아지지 않도록

        responseDTO.getBody().setTotalPages(totalPages);
        responseDTO.getBody().setCurrentGroup(currentGroup);
        responseDTO.getBody().setNextGroupStartPage(nextGroupStartPage);
        responseDTO.getBody().setPrevGroupStartPage(prevGroupStartPage);
        responseDTO.getBody().setCurrentGroupStartPage(currentGroupStartPage);
        responseDTO.getBody().setCurrentGroupEndPage(currentGroupEndPage);


        return responseDTO;


    }


}
