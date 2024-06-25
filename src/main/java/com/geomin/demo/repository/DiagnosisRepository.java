package com.geomin.demo.repository;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DiagnosisRepository {

    int getLastDiagnosisId();

    int addOnlySymptoms(int patientId, int departmentId, String symptoms, int lastDiagnosisId);



}
