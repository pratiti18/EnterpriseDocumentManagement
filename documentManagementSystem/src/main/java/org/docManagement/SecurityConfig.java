package org.docManagement;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http
		.csrf()
		.disable()
		.authorizeHttpRequests()
		.requestMatchers("/swagger-ui/**","/swagger-ui.html",
				"/v2/api-docs",
				"/v3/api-docs",
				"/v2/api-docs/**",
				"/swagger-resources/**","/swagger-resources","/configuration/ui",
				"/configuration/security",
				"/webjars/**",
				"/api/v1/auth/**","/sign/In").permitAll()
		.anyRequest()
		.authenticated();
		
		http
		.oauth2ResourceServer()
		.jwt();
		
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		return http.build();
		
	}

}
