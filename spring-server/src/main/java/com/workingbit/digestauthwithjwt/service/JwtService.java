package com.workingbit.digestauthwithjwt.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.stream.Collectors.joining;

@Service
public class JwtService {

  private final Logger logger = LoggerFactory.getLogger(JwtService.class);

  @Value("${jwsAlgorithmName}")
  private String jwsAlgorithmName;
  @Value("${jwsAlgorithmRequirement}")
  private String jwsAlgorithmRequirement;
  @Value("${secretKeyAlgorithm}")
  private String secretKeyAlgorithm;
  @Value("${accessTokenExpirationMinutes}")
  private Integer accessTokenExpirationMinutes;
  @Value("${refreshTokenExpirationHours}")
  private Integer refreshTokenExpirationHours;
  @Value("${tokenIssuer}")
  private String tokenIssuer;
  @Value("${tokenSecret}")
  private String tokenSecret;
  @Value("${authoritiesClaim}")
  private String authoritiesClaim;

  private JWSAlgorithm jwsAlgorithm;

  @PostConstruct
  public void init() {
    Requirement requirement = null;
    if (jwsAlgorithmRequirement != null) {
      requirement = Requirement.valueOf(jwsAlgorithmRequirement);
    }
    jwsAlgorithm = new JWSAlgorithm(jwsAlgorithmName, requirement);
  }

  public Authentication getUsernamePasswordAuthenticationToken(JWTClaimsSet claimsSet) {
    String subject = claimsSet.getSubject();
    String auths = (String) claimsSet.getClaim(authoritiesClaim);
    List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(auths.split(","));
    return new UsernamePasswordAuthenticationToken(subject, null, authorities);
  }

  public Map<String, String> generateToken(String principal, Collection<? extends GrantedAuthority> authorities) {
    String accessToken = generateAccessToken(principal, new ArrayList<>(authorities));
    String refreshToken = generateRefreshToken(principal, new ArrayList<>(authorities));
    return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
  }

  public String generateAccessToken(String principal, List<GrantedAuthority> authorities) {
    return generateToken(principal, authorities, accessTokenExpirationMinutes);
  }

  public String generateRefreshToken(String principal, List<GrantedAuthority> authorities) {
    return generateToken(principal, authorities, refreshTokenExpirationHours * 60);
  }

  private String generateToken(String principal, List<GrantedAuthority> authorities, Integer expirationMinutes) {
    Date expirationTime = Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES));
    String authoritiesClaimStr = authorities
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(joining(","));
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(principal)
        .issuer(tokenIssuer)
        .expirationTime(expirationTime)
        .claim(authoritiesClaim, authoritiesClaimStr)
        .build();

    SignedJWT signedJWT = new SignedJWT(new JWSHeader(jwsAlgorithm), claimsSet);
    try {
      final SecretKey key = new SecretKeySpec(tokenSecret.getBytes(), secretKeyAlgorithm);
      signedJWT.sign(new MACSigner(key));
    } catch (JOSEException e) {
      logger.error("ERROR while signing JWT", e);
      return null;
    }

    return signedJWT.serialize();
  }

  public JWTClaimsSet getVerifyAndGetClaim(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(tokenSecret);
      boolean valid = signedJWT.verify(verifier);
      if (valid) {
        ConfigurableJWTProcessor<SimpleSecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector((header, context) -> {
          final SecretKey key = new SecretKeySpec(tokenSecret.getBytes(), secretKeyAlgorithm);
          return List.of(key);
        });
        return jwtProcessor.process(signedJWT, null);
      } else {
        logger.error("ERROR TOKEN invalid " + token);
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
      }
    } catch (ParseException | JOSEException | BadJOSEException e) {
      logger.error("ERROR while verify JWT: " + token, e);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unable to verify token");
    }
  }
}
