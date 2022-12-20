package com.example.oauthjwt.configure.OAuth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.oauthjwt.configure.jwt.Token;
import com.example.oauthjwt.configure.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
//public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        Map<String ,Object > map = oAuth2User.getAttributes();

        Token token = tokenService.generateToken(authentication);
        log.info("{}", token);
        response.addHeader("Authentication", token.getToken());
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/loginSuccess")
                .queryParam("token", token.getToken())
                .queryParam("refreshToken", token.getRefreshToken())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response,targetUrl );
//        writeTokenResponse(response, token);
    }

    private void writeTokenResponse(HttpServletResponse response, Token token) throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        response.addHeader("Authorization", token.getToken());
        response.addHeader("Refresh", token.getRefreshToken());
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(token));
        writer.flush();
    }

}
