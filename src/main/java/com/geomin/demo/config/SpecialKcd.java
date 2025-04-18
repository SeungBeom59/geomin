package com.geomin.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "special")
@Getter
@Setter
// 특정 상병으로 지속 진료하는 경우, 본인부담률 변동
// map<상병코드 , 본인부담률> 이고 동기화를 신경쓰지 않는 hashMap이기 때문에
// service 단에서는 final 로 불변으로 읽기전용으로만 사용할 것!
public class SpecialKcd {

    private Map<String , Double> kcd = new HashMap<>(); // 특별 상병코드와 본인부담률
    private String feePrefix;   // 진찰료 코드 prefix (의원 진찰은 AA154로 시작)

    // 특별 상병코드인지 확인
    public boolean isTargetSpecialKcd(String kcdCode){
        return kcd.containsKey(kcdCode);
    }
    // 특별 상병코드에 따라 본인부담률 적용해야 하는 처방수가인지 확인
    public boolean isTargetFeeCode(String feeCode){
        return feeCode.startsWith(feePrefix);
    }


}
