package com.geomin.demo.dto;

// 페이징 처리 DTO
// 페이징 처리를 너무 못한다.
// 페이징은 [현재 페이지], [보여줄 객체 갯수], [총 객체 갯수] 만 있다면 필요한 값들을 구할 수 있다.
// 시작페이지, 끝페이지, 이전페이지 유무, 다음페이지 유무, 전체 객체 수, 현재 페이지, 페이지당 게시글 표시수 정보

import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingDTO {

    private int total;  // 총 객체 갯수
    private int page;   // 현재 페이지
    private int size;   // 보여주려는 객체 개수
    private int btnCnt; // 보여주려는 페이지 버튼 개수
    private int totalPages; // 총 페이지수
    private int currentGroupPage;  // 페이지 그룹
    private int startPage;
    private int endPage;
    private boolean prev;           // 1보다 현재 페이지가 크다면 true
    private boolean next;   // 현재페이지가 totalPages보다 크지 않다면 true;


    public void init() {
        this.totalPages = (int) Math.ceil((double) total / size);
        this.currentGroupPage = (int) Math.ceil((double) page / btnCnt);
        this.startPage = ((currentGroupPage - 1) * btnCnt) + 1;
        this.endPage = Math.min(currentGroupPage * btnCnt, totalPages);
        this.prev = page > btnCnt;
//        this.next = page + (btnCnt-1) < totalPages;
        this.next = page < totalPages;
    }
}
