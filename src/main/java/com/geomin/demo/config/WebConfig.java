package com.geomin.demo.config;

import com.geomin.demo.resolver.CommonModelArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CommonModelArgumentResolver commonModelArgumentResolver;

    // 레이아웃의 사이드바에 공통으로 제공되는 정보를 model에 담는 작업 중복을 커스텀 리졸버로 처리위해 추가.
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers){
        resolvers.add(commonModelArgumentResolver);
    }
}

