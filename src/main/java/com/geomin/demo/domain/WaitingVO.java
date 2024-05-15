package com.geomin.demo.domain;

import lombok.*;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// 접수대기
public class WaitingVO {

    private int waitingId;  // 접수대기번호(pk)
    private int patientId;

}
