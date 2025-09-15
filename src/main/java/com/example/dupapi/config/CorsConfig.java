package com.example.dupapi.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
  @Bean
  CorsFilter corsFilter(@Value("${app.cors.allowed-origins}") String allowed) {
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(Arrays.asList(allowed.split(",")));
    cfg.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    cfg.setAllowedHeaders(Arrays.asList("Content-Type","Authorization"));
    cfg.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", cfg);
    return new CorsFilter(src);
  }
}
