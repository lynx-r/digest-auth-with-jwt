package com.workingbit.digestauthwithjwt.service;

import com.workingbit.digestauthwithjwt.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.stream.Collectors.joining;

@Service
public class JwtService {

  private static final String BEARER = "Bearer ";

  private final Logger logger = LoggerFactory.getLogger(JwtService.class);

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

  private SecretKey secretKey;

  public static String getTokenFromHeader(String authHeader) {
    if (authHeader != null) {
      boolean matchBearerLength = authHeader.length() > BEARER.length();
      if (matchBearerLength) {
        return authHeader.substring(BEARER.length());
      }
    }
    return "";
  }

  @PostConstruct
  public void init() {
    secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecret));
  }

  public Authentication getUsernamePasswordAuthenticationToken(Jws<Claims> claimsSet) {
    String subject = claimsSet.getBody().getSubject();
    String auths = (String) claimsSet.getBody().get(authoritiesClaim);
    List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(auths.split(","));
    return new UsernamePasswordAuthenticationToken(subject, null, authorities);
  }

  public Map<String, String> generateToken(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    return generateToken(user.getUsername(), user.getAuthorities());
  }

  public Map<String, String> generateToken(String principal, Collection<? extends GrantedAuthority> authorities) {
    String accessToken = generateAccessToken(principal, new ArrayList<>(authorities));
    String refreshToken = generateRefreshToken(principal, new ArrayList<>(authorities));
    return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
  }

  public Map<String, String> updateToken(String header, Authentication authentication) {
    String username = (String) authentication.getPrincipal();
    List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
    var accessToken = generateAccessToken(username, authorities);
    var refreshToken = getTokenFromHeader(header);
    return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
  }

  public String generateAccessToken(String principal, List<GrantedAuthority> authorities) {
    return generateToken(principal, new ArrayList<>(authorities), accessTokenExpirationMinutes);
  }

  public String generateRefreshToken(String principal, List<GrantedAuthority> authorities) {
    return generateToken(principal, authorities, refreshTokenExpirationHours * 5);
  }

  private String generateToken(String principal, List<GrantedAuthority> authorities, Integer expirationMinutes) {
    Date expirationTime = Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES));
    String authoritiesClaimValue = authorities
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(joining(","));
    return Jwts.builder()
        .setSubject(principal)
        .setIssuer(tokenIssuer)
        .setExpiration(expirationTime)
        .setIssuedAt(new Date())
        .claim(authoritiesClaim, authoritiesClaimValue)
        .signWith(secretKey)
        .compact();
  }

  public Jws<Claims> getVerifyAndGetClaim(String jwt) throws AuthenticationException {
    return Jwts.parserBuilder()
        .requireIssuer(tokenIssuer)
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(jwt);
  }

}
