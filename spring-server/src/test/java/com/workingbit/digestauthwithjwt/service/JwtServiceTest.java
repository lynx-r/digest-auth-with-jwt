package com.workingbit.digestauthwithjwt.service;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;

@SpringBootTest
class JwtServiceTest {


  @Test
  void generateSecretKey() {
    SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    System.out.println("???");
    System.out.println(Encoders.BASE64.encode(key.getEncoded()));
  }

}
