package com.geomin.demo.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

// 로그인 성공 핸들러
@Slf4j
@Service
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication auth) throws IOException, ServletException {



        // 기본 uri
        String uri = "/reception";

        // security에 의해 인터셉트 당한 경우 데이터 얻어오기
        RequestCache requestCache = new HttpSessionRequestCache();
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        log.info("****************************************************************************************");
        log.info("requestCache::{}",requestCache);  // requestCache::org.springframework.security.web.savedrequest.HttpSessionRequestCache@73e33bcd
        log.info("savedRequest::{}",savedRequest);  // savedRequest::DefaultSavedRequest [http://localhost:8080/이동하려던 주소?continue]

        // 강제로 인터셉트 당해서 왔다면 null이 아님
        if(savedRequest != null){
            log.info(savedRequest.getRedirectUrl());
            uri = savedRequest.getRedirectUrl();
        }

        response.sendRedirect(uri);

    }
}
