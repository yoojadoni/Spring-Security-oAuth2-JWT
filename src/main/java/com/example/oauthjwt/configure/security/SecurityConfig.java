package com.example.oauthjwt.configure.security;
import com.example.oauthjwt.configure.OAuth2.CustomOAuth2UserService;
import com.example.oauthjwt.configure.OAuth2.OAuth2SuccessHandler;
import com.example.oauthjwt.configure.jwt.JwtAuthenticationFilter;
import com.example.oauthjwt.configure.jwt.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//@ConditionalOnBean(ClientRegistrationRepository.class)
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    OAuth2SuccessHandler oAuth2SuccessHandler;


    // 인증 (Authentication) 과 인가 (Authorization).
    // https://velog.io/@tmdgh0221/Spring-Security-%EC%99%80-OAuth-2.0-%EC%99%80-JWT-%EC%9D%98-%EC%BD%9C%EB%9D%BC%EB%B3%B4
    // https://ozofweird.tistory.com/entry/Spring-Boot-Spring-Boot-JWT-OAuth2-2 <-- 쿠키인증임
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(tokenService);
        return http
                // CORS 허용
                .cors()
                .and()
                // 토큰 사용을 위해 Session 비활성화
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .csrf().disable()
                // 로그인 폼 비활성화
                    .formLogin().disable()
                // 기본 로그인 창 비활성화
                    .httpBasic().disable()
                    .authorizeRequests()
                    .antMatchers("/"
                            ,"/oauth/**"
                            /*,"/api/**"*/
                            ,"/resources/static/img/**"
                            ,"/static/img/**"
                            ,"/img/**"
                            ,"/h2-console/**"
                    )
                    .permitAll()
                    .anyRequest().authenticated()
                .and()
                    .oauth2Login()
                        .loginPage("/oauth/login")
                        .authorizationEndpoint()
                            .baseUri("/oauth2/authorization")
//                        .authorizationRequestRepository(cookieAuthorizationRequestRepositort())
                        .and()
                        .userInfoEndpoint()
                            .userService(customOAuth2UserService)
                        .and()
                        .redirectionEndpoint()
                            .baseUri("/login/oauth2/code/*") // 소셜로그인 완료후 리다이렉트될 uri
                        .and()
                        .successHandler(oAuth2SuccessHandler)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                   // .addFilter(new JwtAuthenticationFilter())
                    .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

}