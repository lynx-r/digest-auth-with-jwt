package com.workingbit.digestauthwithjwt.controller;

import com.workingbit.digestauthwithjwt.domain.User;
import com.workingbit.digestauthwithjwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final JwtService jwtService;

  @PostMapping("token")
  @PreAuthorize("hasAnyRole('GUEST', 'ADMIN')")
  public ResponseEntity<Map<String, String>> getToken(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    String token = jwtService.generateToken(user.getUsername(), user.getUsername(), user.getAuthorities());
    return ResponseEntity.ok(Map.of("token", token));
  }

  @GetMapping("authenticated")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Map<String, Boolean>> isAuthenticated() {
    return ResponseEntity.ok(Map.of("isLoggedIn", true));
  }

}
