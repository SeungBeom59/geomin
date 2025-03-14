package com.geomin.demo.resolver;

import com.geomin.demo.dto.UserSecurityDTO;
import com.geomin.demo.dto.WaitingDTO;
import com.geomin.demo.service.UserService;
import com.geomin.demo.service.WaitingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommonModelArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserService userService;
    private final WaitingService waitingService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 어노테이션(타켓이 메소드) 확인을 위해 파라미터로부터 메소드 가져와 확인
        // 필요한 인자들(principal , pageable, model) 유무 확인
        // -> 리졸버 지원유무 결정
        Method method = parameter.getMethod();

        if(method != null && method.isAnnotationPresent(CommonModel.class)){

            boolean isModel = false;
            boolean isPageable = false;

            Parameter[] parameters = method.getParameters();

            for(Parameter p : parameters){
                if(p.getType().equals(Model.class)){
                    isModel = true;
                }

                if(p.getType().equals(Pageable.class)){
                    isPageable = true;
                }
            }

            return isModel && isPageable;
        }

        return false;
    }

    // fixme: 다른 페이지에서 보고 있던 사이드바의 상태가 접수대기환자가 아닌 수납대기환자라면?
    // 그에 맞게 앞단에서 어떤 상태를 보고 있었는지 응답을 받아 분기처리로 그에 맞는 정보반환 필요.
    // 보고 있던 페이지도 반환하여 사이드바 상태 유지되도록 해야 함.

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        log.info("내가 정의한 resolveArgument가 활성화@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        Principal principal = webRequest.getUserPrincipal();
        UserSecurityDTO user = userService.getUser(principal.getName());

        log.info("user::{}",user);

        Pageable pageable = (Pageable) webRequest.getAttribute(Pageable.class.getName() , NativeWebRequest.SCOPE_REQUEST);

        Page<WaitingDTO> waitingList = null;

        if(pageable != null){
             waitingList = waitingService.getWaitingList(pageable , user.getDepartmentId());
        }
        else{
            pageable = PageRequest.of(0,4);
            waitingList = waitingService.getWaitingList(pageable , user.getDepartmentId());
        }

        int waitingEnd = waitingService.getEndCount(user.getDepartmentId());

        Model model = (Model) mavContainer.getModel();

        model.addAttribute("user" , user);
        model.addAttribute("waitingList" , waitingList);
        model.addAttribute("waitingEnd" , waitingEnd);



        return null;
    }
}
