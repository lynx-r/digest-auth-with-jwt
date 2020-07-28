package com.workingbit.digestauthwithjwt.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DigestAuthFilter extends DigestAuthenticationFilter {

  @Value("${realmKey}")
  private String realmKey;
  @Value("${realmName}")
  private String realmName;

  public DigestAuthFilter(UserDetailsService userDetailsService) {
    setUserDetailsService(userDetailsService);
  }

  @PostConstruct
  private void init() {
    var entryPoint = new DigestAuthenticationEntryPoint();
    entryPoint.setKey(realmKey);
    entryPoint.setRealmName(realmName);
    setAuthenticationEntryPoint(entryPoint);
  }

}
