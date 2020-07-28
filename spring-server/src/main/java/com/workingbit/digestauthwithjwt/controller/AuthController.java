package com.workingbit.digestauthwithjwt.controller;

import com.workingbit.digestauthwithjwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final JwtService jwtService;

  @GetMapping("csrf")
  public CsrfToken csrf(CsrfToken token) {
    return token;
  }

  @PostMapping("token")
  @PreAuthorize("hasAnyRole('GUEST', 'ADMIN')")
  public ResponseEntity<Map<String, String>> getToken(Authentication authentication) {
    var token = jwtService.generateToken(authentication);
    return ResponseEntity.ok(token);
  }

  @PostMapping("token/refresh")
  @PreAuthorize("hasAnyRole('GUEST', 'ADMIN')")
  public ResponseEntity<Map<String, String>> getRefreshToken(
      @RequestHeader String authorization,
      Authentication authentication
  ) {
    var token = jwtService.updateToken(authorization, authentication);
    return ResponseEntity.ok(token);
  }

  @GetMapping("authenticated")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Map<String, Boolean>> isAuthenticated() {
    return ResponseEntity.ok(Map.of("isLoggedIn", true));
  }

}
