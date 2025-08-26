package com.github.chiarelli.taskmanager.spring_boot_run.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.github.chiarelli.taskmanager.presentation.security.JwtAuthenticationFilter;

@Configuration
public class AuthConfiguration {

  private final JwtAuthenticationFilter jwtFilter;

  public AuthConfiguration(JwtAuthenticationFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.formLogin(login -> login.disable()) // 🔴 Desabilita login form
				.httpBasic(basic -> basic.disable()) // 🔴 Desabilita Basic Auth
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/**").permitAll() // libera endpoints públicos
						.anyRequest().authenticated() // protege os demais
				);

		return http.build();
	}

  @Bean
	FilterRegistrationBean<JwtAuthenticationFilter> registrationFilter() {
		//Registrando o filter criado para autenticação
		var filter = new FilterRegistrationBean<JwtAuthenticationFilter>();
		filter.setFilter(jwtFilter);
		
		//Aplicando o filtro para todos os endpoints da API]
		filter.addUrlPatterns("/api/*");
		
		//retornar o filter
		return filter;
	}

}
