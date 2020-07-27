package com.workingbit.digestauthwithjwt.auth;

import com.workingbit.digestauthwithjwt.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

import static com.workingbit.digestauthwithjwt.service.JwtService.getTokenFromHeader;
import static java.util.stream.Collectors.toList;

@Component
public class JwtAuthFilter extends AbstractAuthenticationProcessingFilter {

  private final JwtService jwtService;

  @Value("${jwtTokenMatchUrls}")
  private String[] jwtTokenMatchUrls;

  public JwtAuthFilter(JwtService jwtService) {
    super("none");
    this.jwtService = jwtService;
  }

  private static AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
    return new ProviderManager(daoAuthProvider);
  }

  private static AuthenticationSuccessHandler authenticationSuccessHandler() {
    return (request, response, authentication) ->
        request.getRequestDispatcher(request.getRequestURI()).forward(request, response);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    String token = getTokenFromHeader(authHeader);
    if (!token.isEmpty()) {
      var claim = jwtService.getVerifyAndGetClaim(token);
      return jwtService.getUsernamePasswordAuthenticationToken(claim);
    } else {
      throw new BadCredentialsException("Invalid token");
    }
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

}
