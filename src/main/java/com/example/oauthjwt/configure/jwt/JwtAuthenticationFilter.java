package com.example.oauthjwt.configure.jwt;

import com.example.oauthjwt.configure.CustomUserDetailsService;
import com.example.oauthjwt.configure.exception.ExceptionCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
/*
모든 Controller 호출시 처음한번 실행되는 Filter
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${jwt.token.header}")
    private String tokenHeader;

    private TokenService tokenService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    public JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = getJwtFromRequest(request);
        try {
            if (jwtToken != null) {
                String userName = tokenService.getUserName(jwtToken);
                if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (tokenService.validateToken(jwtToken)) {
                        Authentication authentication = tokenService.getAuthentication(jwtToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (SecurityException e) {
            request.setAttribute("exception", ExceptionCode.WRONG_TYPE_TOKEN.getMessage());
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", ExceptionCode.WRONG_TYPE_TOKEN.getMessage());
        } catch (UsernameNotFoundException e) {
            request.setAttribute("exception", ExceptionCode.USER_NOT_FOUND.getMessage());
        } catch (UnsupportedJwtException e){
            request.setAttribute("exception", ExceptionCode.UNSUPPORTED_TOKEN.getMessage());
        } catch (Exception e){
            log.error("{}", e.getMessage());
            request.setAttribute("exception", ExceptionCode.UNKNOWN_ERROR.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}
