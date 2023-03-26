package me.silvernine.jwttutorial.jwt;


import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
// 필요한 권한이 존재하지 않는 경우 403 리턴을 위한 클래스(토큰이 유효하다만 그 토큰의 역할이 맞지 않을 떄)
//JwtProvider가 JwtFilter업무 중 문제가 생겼을 떄 처리할 처리 기준(업무 방침)
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
