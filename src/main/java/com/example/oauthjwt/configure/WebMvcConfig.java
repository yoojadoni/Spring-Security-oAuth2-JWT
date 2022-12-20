package com.example.oauthjwt.configure;


import com.example.oauthjwt.configure.interceptor.handlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {


    public static final String ALLOWED_METHOD_NAMES = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH";

    //CORS활성화처리
    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods(ALLOWED_METHOD_NAMES.split(","))
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                ;

    }


    //인터셉터가 동작 해야 될 요청 주소 mapping 목록
    private static final List<String> URL_PATTERNS
            = Arrays.asList("/async/*", "/api/*", "/user");

/*    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new handlerInterceptor())
                //.addPathPatterns(URL_PATTERNS);
//                .addPathPatterns("/")
                .addPathPatterns("/api/**")
                .excludePathPatterns("/login/**")
                .excludePathPatterns("/css/**")
                .excludePathPatterns("/h2-console/**")
                .excludePathPatterns(("/img/**"))
                ;
    }*/
}
