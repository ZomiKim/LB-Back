package com.study.lastlayer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebCorsConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 모든 경로 허용
				.allowedOrigins("*") // 모든 Origin 허용
				.allowedMethods("*") // 모든 HTTP 메서드 허용 (GET, POST, PUT, DELETE 등)
				.allowedHeaders("*") // 모든 헤더 허용
				.allowCredentials(false); // 인증정보(Cookie, Authorization 헤더 등) 미포함
	}
}
