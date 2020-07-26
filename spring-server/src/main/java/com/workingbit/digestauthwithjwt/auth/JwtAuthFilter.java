package com.workingbit.digestauthwithjwt.auth;

import com.workingbit.digestauthwithjwt.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter({"/api/protected/metrics**", "/api/auth/authenticated"})
public class JwtAuthFilter extends AbstractAuthenticationProcessingFilter {
  private static final String BEARER = "Bearer ";

  private final JwtService jwtService;

  public JwtAuthFilter(JwtService jwtService) {
    super(new OrRequestMatcher(new AntPathRequestMatcher("/api/protected/metrics**"),
        new AntPathRequestMatcher("/api/auth/authenticated")));
    this.jwtService = jwtService;
    setAuthenticationManager(authenticationManager());
    setAuthenticationSuccessHandler(authenticationSuccessHandler());
  }

  private static AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
    return new ProviderManager(daoAuthProvider);
  }

  private static AuthenticationSuccessHandler authenticationSuccessHandler() {
    return new SimpleUrlAuthenticationSuccessHandler() {
      @Override
      public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        request.getRequestDispatcher(request.getRequestURI()).forward(request, response);
        clearAuthenticationAttributes(request);
      }
    };
  }

  private static String getTokenFromHeader(String authHeader) {
    if (authHeader != null) {
      boolean matchBearerLength = authHeader.length() > BEARER.length();
      if (matchBearerLength) {
        return authHeader.substring(BEARER.length());
      }
    }
    return "";
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    String token = getTokenFromHeader(authHeader);
    if (!token.isEmpty()) {
      var claim = jwtService.getVerifyAndGetClaim(token);
      return jwtService.getUsernamePasswordAuthenticationToken(claim);
    } else {
      throw new BadCredentialsException("Invalid token");
    }

  }

}
