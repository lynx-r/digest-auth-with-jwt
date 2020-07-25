package com.workingbit.digestauthwithjwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/public")
public class PublicController {

  @GetMapping("")
  public ResponseEntity<Map<String, String>> getGreeting() {
    return ResponseEntity.ok(Map.of("greeting", "Welcome!"));
  }

}
