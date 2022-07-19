package com.moti.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moti.web.SessionConst;
import com.moti.web.exceptionhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        log.info("로그인 체크 인터셉터 - requestURI: {}", requestURI);

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SessionConst.LOGIN_USER) == null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResult errorResult = ErrorResult.builder()
                    .code("401")
                    .message("인증 필요")
                    .build();
            response.getWriter().write(objectMapper.writeValueAsString(errorResult));
            return false;
        }

        return true;
    }
}
