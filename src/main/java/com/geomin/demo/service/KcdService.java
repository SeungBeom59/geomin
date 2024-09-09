package com.geomin.demo.service;

import com.geomin.demo.dto.KcdDTO;

import java.util.List;

public interface KcdService {

    List<KcdDTO> getKcdListById(int kcdId);

    int addKcd(List<KcdDTO> kcds);

    int deleteAndCreateKcds(int kcdId, List<KcdDTO> kcds);
}
