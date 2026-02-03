package com.study.lastlayer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.study.lastlayer.auth.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // 메서드 단위 보안 활성화
public class SecurityConfig {
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
		return http.csrf(csrf -> csrf.disable()) // JWT는 stateless하므로 csrf(Cross-Site Request Forgery, 사이트 간 요청 위조) 불필요
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()).formLogin(form -> form.disable())
				.httpBasic(httpBasic -> httpBasic.disable())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.sessionManagement(
						sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 서버가 세션을 관리하지 않겠다
				.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		// BCrypt는 암호화할 때마다 내부적으로 랜덤한 **Salt(소금)**를 생성하여 결과값이 매번 다르게 나옴.
		return new BCryptPasswordEncoder();
	}
}
