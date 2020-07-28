package com.workingbit.digestauthwithjwt.service;

import com.workingbit.digestauthwithjwt.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Service
public class JwtService {

  private static final String BEARER = "Bearer ";

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

  @PostConstruct
  public void init() {
    secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecret));
  }

  private static String getTokenFromHeader(String authHeader) {
    if (authHeader != null) {
      return authHeader.replace(BEARER, "");
    }
    return "";
  }

  private static String authoritiesToString(Collection<? extends GrantedAuthority> authorities) {
    return authorities
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(joining(","));
  }

  public Authentication createAuthenticationFromHeader(String header) {
    String token = getTokenFromHeader(header);

    var claims = getJwsClaims(token).getBody();
    String subject = claims.getSubject();
    String auths = (String) claims.get(authoritiesClaim);
    List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(auths);

    return new UsernamePasswordAuthenticationToken(subject, null, authorities);
  }

  public Map<String, String> generateToken(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    String principal = user.getUsername();
    String authorities = authoritiesToString(user.getAuthorities());

    String accessToken = generateAccessToken(principal, authorities);
    String refreshToken = generateRefreshToken(principal, authorities);

    return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
  }

  public Map<String, String> updateToken(String header, Authentication authentication) {
    String username = (String) authentication.getPrincipal();
    var authorities = authoritiesToString(authentication.getAuthorities());
    var accessToken = generateAccessToken(username, authorities);
    var refreshToken = getTokenFromHeader(header);

    return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
  }

  private String generateAccessToken(String principal, String authorities) {
    return generateToken(principal, authorities, accessTokenExpirationMinutes);
  }

  private String generateRefreshToken(String principal, String authorities) {
    return generateToken(principal, authorities, refreshTokenExpirationHours * 5);
  }

  private String generateToken(String principal, String authorities, Integer expirationMinutes) {
    Date expirationTime = Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES));
    return Jwts.builder()
        .setSubject(principal)
        .setIssuer(tokenIssuer)
        .setExpiration(expirationTime)
        .setIssuedAt(new Date())
        .claim(authoritiesClaim, authorities)
        .signWith(secretKey)
        .compact();
  }

  private Jws<Claims> getJwsClaims(String jwt) throws AuthenticationException {
    return Jwts.parserBuilder()
        .requireIssuer(tokenIssuer)
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(jwt);
  }
}
