//package com.geomin.demo.config;
//
//import jakarta.servlet.RequestDispatcher;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.web.servlet.error.ErrorController;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@Controller
//@Slf4j
//public class WebErrorController implements ErrorController {
//
//    @GetMapping("/error")
//    public String handleError(HttpServletRequest req){
//
//        log.info("Get 요청 /error >>> handleError()실행됨.");
//
//        Object status = req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//
//        if(status != null){
//            int statusCode = Integer.valueOf(status.toString());
//
//            if(statusCode == HttpStatus.NOT_FOUND.value()){
//                return "error/404";
//            }
//            else if(statusCode == HttpStatus.BAD_REQUEST.value()){
//                return "error/404";
//            }
//            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()){
//                return "error/404";
//            }
//            else{
//                return "/login";
//            }
//        }
//
//        return "/login";
//
//    }
//}

