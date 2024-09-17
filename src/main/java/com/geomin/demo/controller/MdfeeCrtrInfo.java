package com.geomin.demo.controller;

import com.geomin.demo.dto.BillDTO;
import com.geomin.demo.dto.PagingDTO;
import com.geomin.demo.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// 수가기준정보조회서비스
@Controller
@Slf4j
public class MdfeeCrtrInfo {

    @Value("${apis.data.mdfeeCrtrInfoService}")
    private String key;

    public ResponseDTO getApi(String keyword , String code , int page) throws IOException {

        int pageSize = 5;

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B551182/mdfeeCrtrInfoService/getDiagnossMdfeeList"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + URLEncoder.encode(key , "UTF-8")); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + pageSize); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + page); /*페이지 번호*/

        if(keyword != null){
            urlBuilder.append("&" + URLEncoder.encode("korNm","UTF-8") + "=" + URLEncoder.encode("재진", "UTF-8")); /*수가 한글명 (검색 유형 : %A%)*/
        }
        else if(code != null){
            urlBuilder.append("&" + URLEncoder.encode("mdfeeCd","UTF-8") + "=" + URLEncoder.encode("M6561", "UTF-8")); /*수가코드(검색 유형 : A%)*/
        }
//        urlBuilder.append("&" + URLEncoder.encode("mdfeeDivNo","UTF-8") + "=" + URLEncoder.encode("자656가", "UTF-8")); /*수가코드에 대한 분류번호 (검색 유형 : A%)*/


        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader br;
        ResponseDTO responseDTO = null;

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();   // 응답을 담기 위한 문자열빌더를 만들고

            // 서버로부터 받았던 응답을 담고 있는 br로부터 한 줄씩 inputLine에 담아가며
            // 문자열빌더에 담아넣어라. null을 만나기 전까지.
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close(); // 마지막으로 버퍼리더는 닫아라.
            System.out.println(response.toString());
            responseDTO = parseXML(response.toString());

        } else {
            System.out.println(conn.getResponseMessage());
            System.out.println("GET request failed, Response code: " + conn.getResponseCode());
        }

        conn.disconnect();

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
            List<BillDTO> bills = new ArrayList<>();

            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;

                    String feeCode = element.getElementsByTagName("mdfeeCd").item(0).getTextContent();
                    String codeName = element.getElementsByTagName("korNm").item(0).getTextContent();
                    String feeDivNum = element.getElementsByTagName("mdfeeDivNo").item(0).getTextContent();
                    String sd = element.getElementsByTagName("adtStaDd").item(0).getTextContent();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date startDate = format.parse(sd);

                    boolean benefitType = element.getElementsByTagName("payTpCd").item(0).getTextContent().equals("1");
                    boolean surgeryYn = element.getElementsByTagName("soprTpNm").item(0).getTextContent().equals("1");
                    boolean deductibleA = element.getElementsByTagName("slfBrdnRtCzaApvYn").item(0).getTextContent().equals("1");
                    boolean deductibleB = element.getElementsByTagName("slfBrdnRtCzaBpvYn").item(0).getTextContent().equals("1");
                    int unitPrice = Integer.parseInt(element.getElementsByTagName("unprc2").item(0).getTextContent());
                    double costScore = Double.parseDouble(element.getElementsByTagName("cvalPnt").item(0).getTextContent());

                    BillDTO billDTO = BillDTO.builder()
                            .startDate(startDate)
                            .benefitType(benefitType)
                            .unitPrice(unitPrice)
                            .costScore(costScore)
                            .feeDivNum(feeDivNum)
                            .feeCode(feeCode)
                            .codeName(codeName)
                            .surgeryYn(surgeryYn)
                            .deductibleA(deductibleA)
                            .deductibleB(deductibleB)
                            .build();

                    bills.add(billDTO);
                }
            }

            responseDTO.setBillDTOList(bills);
            log.info("bills::{}", bills);

            return responseDTO;
        }
        catch (Exception e) {
            log.error("Exception [Err_Location] : {}", e.getStackTrace()[0]);
            log.error("Exception [Err_Msg]: {}", e.getMessage());
        }

        return null;
    }

}