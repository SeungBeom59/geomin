package com.geomin.demo.resolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
// 공통으로 제공되는 모델 데이터 제공 식별용 어노테이션
public @interface CommonModel {
}
