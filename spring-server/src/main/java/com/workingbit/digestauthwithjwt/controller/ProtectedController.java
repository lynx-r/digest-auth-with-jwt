package com.workingbit.digestauthwithjwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/protected")
public class ProtectedController {

  @GetMapping("metrics")
  @PreAuthorize("hasRole('ADMIN')")
  public Object getMetrics() {
    return ResponseEntity.ok(Map.of("mood", "good"));
  }

}
