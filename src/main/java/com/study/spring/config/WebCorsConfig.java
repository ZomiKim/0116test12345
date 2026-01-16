package com.study.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebCorsConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
			.allowedOriginPatterns("http://localhost:*")
			.allowedMethods("GET", "POST", "PUT", "DELETE")
			.allowedHeaders("Content-Type", "Authorization")
			.exposedHeaders("Location") // 헤더에 location값은 있어도 된다는 것을 의미 
			.allowCredentials(true) // 쿠키 허용
			.maxAge(3600);
	}
}
