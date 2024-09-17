package com.geomin.demo.controller;

import com.geomin.demo.dto.KcdDTO;
import com.geomin.demo.dto.PagingDTO;
import com.geomin.demo.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

// 공공데이터
// 건강보험심사평가원 : 질병정보서비스 api
@Controller
@Slf4j
public class ApiExplorer2 {

    @Value("${apis.data.kcd.key}")
    private String key;

    @GetMapping("/kcd-search")
    public String getKcdPopup(@RequestParam(name = "keyword" , defaultValue = "") String keyword ,
                              @RequestParam(name = "page" , defaultValue = "1") int page ,
                              @RequestParam(name = "medTp" , defaultValue = "1") int medTp ,
                              @RequestParam(name = "sickType" , defaultValue = "2") String sickType ,
                              @RequestParam(name = "diseaseType" , defaultValue = "SICK_NM") String diseaseType ,
                              Model model) throws IOException {

        log.info("get >> /kcd-search... getKcdPopup() 실행.");

        ResponseDTO apiResult = getApi(keyword , page, medTp , sickType, diseaseType);

        model.addAttribute("apiResult", apiResult);

        model.addAttribute("keyword", keyword);
        model.addAttribute("controller-page", page);
        model.addAttribute("medTp", medTp);
        model.addAttribute("sickType" , sickType);
        model.addAttribute("diseaseType" , diseaseType);

        return "kcd_popup";
    }



    public ResponseDTO getApi(String keyword , int page, int medTp, String sickType, String diseaseType) throws IOException {

        int pageSize = 5;   // 한 페이지 결과수 == numOfRows

        if(keyword == null){
            keyword = "";
        }

        StringBuilder urlBuilder = new StringBuilder("https://apis.data.go.kr/B551182/diseaseInfoService1/getDissNameCodeList1");
        urlBuilder.append("?" + URLEncoder.encode("serviceKey" ,"UTF-8") + "=" + URLEncoder.encode(key , "UTF-8"));     // 서비스 인증키
        urlBuilder.append("&" + URLEncoder.encode("numOfRows" , "UTF-8") + "=" + pageSize); // 한 페이지 결과 수
        urlBuilder.append("&" + URLEncoder.encode("pageNo" , "UTF-8") + "=" + page);  // 페이지 번호
        urlBuilder.append("&" + URLEncoder.encode("sickType" , "UTF-8") + "=" + sickType); // 상병 구분(1: 3단 상병, 2: 4단 상병)
        urlBuilder.append("&" + URLEncoder.encode("medTp" , "UTF-8") + "=" + medTp);    // 양방, 한방 구분(1: 양방, 2: 한방)
        urlBuilder.append("&" + URLEncoder.encode("diseaseType" , "UTF-8") + "=" + diseaseType); // 질병검색타입(SICK_CD: 상병코드, SICK_NM: 상병명)
        urlBuilder.append("&" + URLEncoder.encode("searchText" , "UTF-8") + "=" + URLEncoder.encode(keyword , "UTF-8"));    // 검색어

        URL url = new URL(urlBuilder.toString());   // url 객체 생성
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // 서버 연결 설정(통신 설정)
        conn.setRequestMethod("GET");   // 어떤 메소드로 보낼지 설정
        System.out.println("Response code: " + conn.getResponseCode()); // 서버로 요청을 보내고 응답을 받아온다.

        BufferedReader br;
        ResponseDTO responseDTO = null;

        // 200(ok) 응답이라면
        if (conn.getResponseCode() == 200) {
            // 서버로부터 받은 응답은 바이트 스트림 형태이므로 문자열로 변경해주고 버퍼에 담는다.
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();   // 응답을 담기 위한 문자열빌더를 만들고

            // 서버로부터 받았던 응답을 담고 있는 br로부터 한 줄씩 inputLine에 담아가며
            // 문자열빌더에 담아넣어라. null을 만나기 전까지.
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close(); // 마지막으로 버퍼리더는 닫아라.

//            System.out.println(response.toString());    // 응답을 출력해봐라.

            responseDTO = parseXML(response.toString());

//            System.out.println(Objects.requireNonNull(responseDTO).getKcdDTOList());
        }
        else {
            System.out.println(conn.getResponseMessage());
            System.out.println("GET request failed, Response code: " + conn.getResponseCode());
        }

        conn.disconnect(); // 서버 통신 끊어줌.

        return responseDTO;
    }

    public ResponseDTO parseXML(String xml){


        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
            doc.getDocumentElement().normalize();

            String numOfRows = doc.getElementsByTagName("numOfRows").item(0).getTextContent();
            String pageNo = doc.getElementsByTagName("pageNo").item(0).getTextContent();
            String totalCount = doc.getElementsByTagName("totalCount").item(0).getTextContent();

            System.out.println("numOfRows: " + numOfRows);
            System.out.println("pageNo: " + pageNo);
            System.out.println("totalCount: " + totalCount);

            // xml 문서에서 빼낸 문자열 int 변환
            int total = Integer.parseInt(totalCount); // 총 kcd 갯수
            int size = Integer.parseInt(numOfRows); // 보여주는 kcd 갯수
            int page = Integer.parseInt(pageNo);     // 현재 페이지

            PagingDTO pagingDTO = new PagingDTO();
            pagingDTO.setTotal(total);
            pagingDTO.setSize(size);
            pagingDTO.setPage(page);
            pagingDTO.setBtnCnt(5);
            pagingDTO.init();

            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setPagingDTO(pagingDTO);

            NodeList nodeList = doc.getDocumentElement().getElementsByTagName("item");
            List<KcdDTO> kcds = new ArrayList<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String sickCd = element.getElementsByTagName("sickCd").item(0).getTextContent();
                    String sickEngNm = element.getElementsByTagName("sickEngNm").item(0).getTextContent();
                    String sickNm = element.getElementsByTagName("sickNm").item(0).getTextContent();

                    KcdDTO kcdDTO = KcdDTO.builder()
                            .kcdCode(sickCd)
                            .kcdName(sickNm)
                            .build();

                    kcds.add(kcdDTO);

//                    System.out.println("sickCd: " + sickCd);
//                    System.out.println("sickEngNm: " + sickEngNm);
//                    System.out.println("sickNm: " + sickNm);
                }
            }
            responseDTO.setKcdDTOList(kcds);

            return responseDTO;
        }
        catch (Exception e) {
            log.error("Exception [Err_Location] : {}", e.getStackTrace()[0]);
            log.error("Exception [Err_Msg]: {}", e.getMessage());
        }

        return null;
    }

}
