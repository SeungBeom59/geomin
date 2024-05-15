package com.geomin.demo.dto;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.domain.Pageable;

@Builder
@Data
public class RequestList<T> {

    private T data;                 // 요청 파라미터
    private Pageable pageable;      // 페이지 설정 관련

}
