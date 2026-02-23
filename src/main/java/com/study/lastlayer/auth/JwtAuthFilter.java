package com.study.lastlayer.auth;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;

	public JwtAuthFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			try {
				var claims = jwtUtil.validateToken(token);

				CustomUserPrincipal principal = new CustomUserPrincipal(claims.getSubject(),
						jwtUtil.extractMemberId(claims), jwtUtil.extractRoles(claims));
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null,
						principal.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (Exception e) {
				// 토큰 검증 실패 시 처리
				e.printStackTrace();
			}
		}
		filterChain.doFilter(request, response);
	}
}
