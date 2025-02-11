package com.geomin.demo.dto;

import com.geomin.demo.domain.MedicalBillVO;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
// 진료기록 하나를 보기 위해 가져가야할 모든 dto
public class ResponseDTO {

    private DiagnosisDTO diagnosisDTO;              // 진료기록

    private List<FileInfoDTO> fileInfoDTOList;      // 첨부파일 정보

    private PatientDTO patientDTO;                  // 환자정보

    private List<MedicineDTO> medicineDTOList;      // 처방정보

    private List<DiagnosisDTO> diagnosisDTOList;    // 진료기록 모음

    private List<KcdDTO> kcdDTOList;                // 질병기록 모음

    private List<WaitingDTO> waitingDTOList;        // 진료대기 또는 진료완료 환자 모음
    private Page<WaitingDTO> waitingDTOS;           // 웹 소캣통신에 함께 보낼 진료대기 환자 리스트

    private List<TreatmentDTO> treatmentDTOList;    // 처방수가 기록 모음

    private List<MedicalMaterialDTO> medicalMaterialDTOList;    // 치료재료 모음
    private List<MedicalBillDTO> medicalBillDTOList;            // 치료재료청구 모음

    // 페이징용
    private int page;                   // 페이지
    private int size;                   // 사이즈
    private int totalCount;             // 총 갯수
    private int totalPages;             // 전체 페이지 수
    private int currentGroup;           // 현재 페이지가 속하는 페이지 그룹
    private int prevGroupStartPage;     // 이전 페이지 그룹의 첫번째 페이지
    private int currentGroupStartPage;  // 현재 페이지 그룹의 첫번째 페이지
    private int currentGroupEndPage;    // 현재 페이지 그룹의 마지막 페이지
    private int nextGroupStartPage;     // 다음 페이지 그룹의 첫번째 페이지


    private PagingDTO pagingDTO;        // 페이징

}
