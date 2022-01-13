package com.halildurmus.hotdeals.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.halildurmus.hotdeals.security.models.SecurityProperties;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private SecurityProperties securityProperties;

  @Autowired
  private FirebaseFilter firebaseFilter;

  @Bean
  public AuthenticationEntryPoint restAuthenticationEntryPoint() {
    return (httpServletRequest, httpServletResponse, e) -> {
      final Map<String, Object> errorObject = new HashMap<>();
      errorObject.put("message", "Unauthorized access of protected resource, invalid credentials");
      errorObject.put("error", HttpStatus.UNAUTHORIZED);
      errorObject.put("code", HttpStatus.UNAUTHORIZED.value());
      errorObject.put("timestamp", new Timestamp(new Date().getTime()));
      httpServletResponse.setContentType("application/json;charset=UTF-8");
      httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
      httpServletResponse.getWriter().write(objectMapper.writeValueAsString(errorObject));
    };
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(securityProperties.getAllowedOrigins());
    configuration.setAllowedMethods(securityProperties.getAllowedMethods());
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    final String[] adminAntPatternsGET = {"/actuator/**", "/deals", "/deals/", "/users", "/users/",
        "/users/search/findByEmail"};
    final String[] adminAntPatternsPOST = {"/categories", "/stores"};
    final String[] adminAntPatternsPATCH = {"/users/*"};
    final String[] adminAntPatternsPUT = {"/categories/*", "/stores/*", "/users/*"};
    final String[] adminAntPatternsDELETE = {"/categories/*", "/stores/*", "/users/*"};
    final String[] publicAntPatternsGET = {"/actuator/health", "/categories", "/stores",
        "/users/*/comment-count"};
    final String[] publicAntPatternsPOST = {"/users"};

    httpSecurity.cors().configurationSource(corsConfigurationSource()).and().csrf().disable()
        .formLogin().disable()
        .httpBasic().disable().exceptionHandling()
        .authenticationEntryPoint(restAuthenticationEntryPoint())
        .and().authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .antMatchers("/users/me/**").authenticated()
        .antMatchers("/comments/**", "/deal-reports/**", "/user-reports/**")
        .access("hasRole('ROLE_SUPER')")
        .antMatchers(HttpMethod.GET, publicAntPatternsGET).permitAll()
        .antMatchers(HttpMethod.GET, adminAntPatternsGET).access("hasRole('ROLE_SUPER')")
        .antMatchers(HttpMethod.GET, "/deals/**", "/users/*").permitAll()
        .antMatchers(HttpMethod.POST, publicAntPatternsPOST).permitAll()
        .antMatchers(HttpMethod.POST, adminAntPatternsPOST).access("hasRole('ROLE_SUPER')")
        .antMatchers(HttpMethod.PUT, adminAntPatternsPUT).access("hasRole('ROLE_SUPER')")
        .antMatchers(HttpMethod.DELETE, adminAntPatternsDELETE).access("hasRole('ROLE_SUPER')")
        .antMatchers(HttpMethod.PATCH, adminAntPatternsPATCH).access("hasRole('ROLE_SUPER')")
        .anyRequest().authenticated().and()
        .addFilterBefore(firebaseFilter, BasicAuthenticationFilter.class)
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

}