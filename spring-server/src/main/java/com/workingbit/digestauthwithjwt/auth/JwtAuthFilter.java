package com.workingbit.digestauthwithjwt.auth;

import com.workingbit.digestauthwithjwt.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class JwtAuthFilter extends AbstractAuthenticationProcessingFilter {

  @Value("${jwtTokenMatchUrls}")
  private String[] jwtTokenMatchUrls;

  private final JwtService jwtService;

  public JwtAuthFilter(JwtService jwtService) {
    super("none");
    this.jwtService = jwtService;
  }

  /**
   * Use DaoAuthenticationProvider configured with UserDetailsService
   *
   * @return ProviderManager
   */
  private static AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
    return new ProviderManager(daoAuthProvider);
  }

  /**
   * Redirect after successful authentication to url which is requested
   *
   * @return AuthenticationSuccessHandler
   */
  private static AuthenticationSuccessHandler authenticationSuccessHandler() {
    return (request, response, authentication) ->
        request.getRequestDispatcher(request.getRequestURI()).forward(request, response);
  }

  @PostConstruct
  private void init() {
    List<RequestMatcher> matchers = Arrays.stream(jwtTokenMatchUrls)
        .map(AntPathRequestMatcher::new)
        .collect(toList());
    setRequiresAuthenticationRequestMatcher(new OrRequestMatcher(matchers));
    setAuthenticationManager(authenticationManager());
    setAuthenticationSuccessHandler(authenticationSuccessHandler());
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    return jwtService.createAuthenticationFromHeader(authHeader);
  }

}
