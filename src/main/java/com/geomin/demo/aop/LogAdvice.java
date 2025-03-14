package com.geomin.demo.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Aspect // pointcut + advice = aspect
@Slf4j
public class LogAdvice {

    // 모든 컨트롤러 참조하는 pointcut
    @Pointcut("execution(* com.geomin.demo.controller.*Controller.*(..))")
    public void allPointCut(){};

    // 컨트롤러 호출 전, http 요청 방식 및 컨트롤러 메소드명 로깅하는 advice
    @Before("allPointCut()")
    public void logControllerCall(JoinPoint joinPoint){

        String methodName = joinPoint.getSignature().getName();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes != null){
            HttpServletRequest request = attributes.getRequest();
            String requestMethod = request.getMethod();

            log.info("************************************AOP*****************************************");
            log.info("HTTP 요청 방식 : " + requestMethod + " | [AOP]호출 컨트롤러 : " + methodName);
        }
        else{
            log.info("[AOP]호출 컨트롤러 : " + methodName);
        }
    }


}
