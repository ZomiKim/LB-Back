package com.study.lastlayer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

import com.study.lastlayer.auth.JwtAuthFilter;
import com.study.lastlayer.auth.JwtUtil;

@Configuration
// Spring Data JPA를 사용하며 페이징(Paging) 결과를 클라이언트에게 JSON으로 응답(직렬화, Serialization)할 때 발생하는 아래 WARN 메시지 방지
// WARN:  Serializing PageImpl instances as-is is not supported
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class WebConfig {
	@Value("${lastlayer.jwt-secret}")
	private String jwtSecret;

	@Bean
	JwtUtil jwtUtil() {
		return new JwtUtil(jwtSecret);
	}

	@Bean
	JwtAuthFilter jwtAuthFilter(JwtUtil jwtUtil) {
		return new JwtAuthFilter(jwtUtil);
	}

}
