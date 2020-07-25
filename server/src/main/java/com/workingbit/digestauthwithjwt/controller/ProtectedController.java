package com.workingbit.digestauthwithjwt.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("api/protected")
public class ProtectedController {

  @GetMapping("metrics")
  @PreAuthorize("hasRole('ADMIN')")
  public Object getMetrics() throws IOException {
    InputStream inputStream = getClass().getResourceAsStream("/data/metrics.json");
    return inputStream.read();
  }

}
