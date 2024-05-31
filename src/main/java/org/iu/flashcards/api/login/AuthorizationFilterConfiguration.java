package org.iu.flashcards.api.login;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Base64;

@Configuration
public class AuthorizationFilterConfiguration {

  @Bean
  @Autowired
  public FilterRegistrationBean<AuthorizationFilter> authorizationFilterBean(AuthorizationFilter authorizationFilter) {
    FilterRegistrationBean<AuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(authorizationFilter);
    registrationBean.addUrlPatterns("/*"); // Apply filter to all URLs
    registrationBean.setOrder(1); // Set filter order
    return registrationBean;
  }

  @Bean
  @Scope("singleton")
  @Autowired
  JwtParser sessionDecoder() {
    return Jwts.parserBuilder()
      .setSigningKey(Base64.getEncoder().encodeToString(
        LoginService.JWT_SECRET.getBytes()
      )).build();
  }

  @Bean
  public FilterRegistrationBean<CorsFilter> corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOriginPattern("*"); // Use allowedOriginPatterns instead of allowedOrigins
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    source.registerCorsConfiguration("/**", config);
    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
    bean.setOrder(0); // Set order to execute before the authorization filter
    return bean;
  }
}
