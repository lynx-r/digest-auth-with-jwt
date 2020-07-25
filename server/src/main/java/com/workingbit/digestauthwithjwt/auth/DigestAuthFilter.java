package com.workingbit.digestauthwithjwt.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

import static com.workingbit.digestauthwithjwt.util.CryptoUtils.md5Hex;


@Component
@WebFilter("/api/auth/token")
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
    setAuthenticationEntryPoint(authenticationEntryPoint());
  }

  private DigestAuthenticationEntryPoint authenticationEntryPoint() {
    var entryPoint = digestAuthenticationEntryPoint();
    entryPoint.setKey(realmKey);
    entryPoint.setRealmName(realmName);
    return entryPoint;
  }

  private DigestAuthenticationEntryPoint digestAuthenticationEntryPoint() {
    return new DigestAuthenticationEntryPoint() {
      @Override
      public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {

        // compute a nonce (do not use remote IP address due to proxy farms)
        // format of nonce is:
        // base64(expirationTime + ":" + md5Hex(expirationTime + ":" + key))
        long expiryTime = System.currentTimeMillis() + (getNonceValiditySeconds() * 1000);
        String signatureValue = md5Hex(expiryTime + ":" + getKey());
        String nonceValue = expiryTime + ":" + signatureValue;
        String nonceValueBase64 = new String(Base64.getEncoder().encode(nonceValue.getBytes()));

        // qop is quality of protection, as defined by RFC 2617.
        // we do not use opaque due to IE violation of RFC 2617 in not
        // representing opaque on subsequent requests in same session.
        String authenticateHeader = "Digest realm=\"" + realmName + "\", "
            + "qop=\"auth\", nonce=\"" + nonceValueBase64 + "\"";

        if (authException instanceof NonceExpiredException) {
          authenticateHeader = authenticateHeader + ", stale=\"true\"";
        }

//        if (logger.isDebugEnabled()) {
//          logger.debug("WWW-Authenticate header sent to user agent: "
//              + authenticateHeader);
//        }

        response.addHeader("WWW-Authenticate", authenticateHeader);
        response.setStatus(HttpStatus.OK.value());
      }
    };
  }
}
