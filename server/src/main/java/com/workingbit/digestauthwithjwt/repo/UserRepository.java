package com.workingbit.digestauthwithjwt.repo;

import com.workingbit.digestauthwithjwt.domain.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class UserRepository {

  private final List<User> USERS = Arrays.asList(
      new User("9999999999", "password",
          Arrays.asList(
              new SimpleGrantedAuthority("ROLE_ADMIN"),
              new SimpleGrantedAuthority("ROLE_GUEST")
          )
      ),
      new User("9999999991", "password",
          Collections.singletonList(new SimpleGrantedAuthority("ROLE_GUEST"))
      )
  );

  public User findByEmail(final String email) {
    return USERS.stream().filter(user -> user.getUsername().equals(email)).findFirst().orElseThrow();
  }

}
