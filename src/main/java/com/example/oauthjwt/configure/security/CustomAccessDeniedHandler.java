package com.example.oauthjwt.configure.security;

import com.example.oauthjwt.configure.exception.ExceptionCode;
import com.example.oauthjwt.configure.jwt.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@RequiredArgsConstructor
@Component
/*
서버에 요청을 할 때 액세스가 가능한지 권한을 체크후 액세스 할 수 없는 요청을 했을시 동작된다.
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    TokenService tokenService;


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // FE에서 처리하게 하는 방식으로 오류코드 및 메시지 넘겨주는 형태
        ExceptionCode exceptionCode;
        exceptionCode = ExceptionCode.PERMISSION_DENIED;
        setResponse(response, exceptionCode);
    }

    private void setResponse(HttpServletResponse response, ExceptionCode exceptionCode) throws IOException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JSONObject responseJson = new JSONObject();
        responseJson.put("message", exceptionCode.getMessage());
        responseJson.put("code", exceptionCode.getCode());

        response.getWriter().print(responseJson);
    }
}