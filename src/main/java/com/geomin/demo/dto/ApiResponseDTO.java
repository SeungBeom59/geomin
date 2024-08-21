package com.geomin.demo.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDTO {

    private Header header;
    private Body body;


    @Getter
    @Setter
    @ToString
    public class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Getter
    @Setter
    @ToString
    public class Body {
        private int pageNo;                 // 현재 페이지 수
        private int totalCount;             // 총 갯수
        private int numOfRows;              // 한 페이지 결과 수 (5로 설정해둠) : pageSize
        private int totalPages;             // 전체 페이지 수
        private int currentGroup;           // 현재 페이지가 속하는 페이지 그룹
        private int currentGroupStartPage;  // 현재 페이지 그룹의 첫번째 페이지
        private int currentGroupEndPage;    // 현재 페이지 그룹의 마지막 페이지
        private int nextGroupStartPage;     // 다음 페이지 그룹의 첫번째 페이지
        private int prevGroupStartPage;     // 이전 페이지 그룹의 첫번째 페이지
        private List<MedicineDTO> items;
    }


}

