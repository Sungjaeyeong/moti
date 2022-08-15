package com.moti;

import com.moti.web.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns(
                        "/users/**",
                        "/posts/**",
                        "/comments/**",
                        "/teams/**"
                )
                .excludePathPatterns(
                        "/users",
                        "/login",
                        "/logout"
                );
    }
}
