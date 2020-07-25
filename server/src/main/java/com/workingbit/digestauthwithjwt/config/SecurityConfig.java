package com.workingbit.digestauthwithjwt.config;

import com.workingbit.digestauthwithjwt.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
@ServletComponentScan("ru.hackatonkursk.auth")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${logoutUrl}")
  private String logoutUrl;
  @Value("${whiteListedAuthUrls}")
  private String[] whiteListedAuthUrls;
  @Value("${originUrls}")
  private String[] originUrls;
  @Value("${headers}")
  private String[] headers;
  @Value("${methods}")
  private String[] methods;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .cors();

    http
        .csrf()
        .disable();

    http
        .logout()
        .logoutUrl(logoutUrl)
        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));

    http
        .authorizeRequests()
        .antMatchers(whiteListedAuthUrls)
        .permitAll();
  }

  @Bean
  @Primary
  public UserDetailsService userDetailsService(final UserRepository users) {
    return users::findByEmail;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowCredentials(true);
    configuration.setAllowedOrigins(Arrays.asList(originUrls));
    configuration.setAllowedMethods(Arrays.asList(methods));
    configuration.setAllowedHeaders(Arrays.asList(headers));
    configuration.setExposedHeaders(Arrays.asList(headers));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
