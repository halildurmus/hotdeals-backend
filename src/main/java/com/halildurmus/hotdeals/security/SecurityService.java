package com.halildurmus.hotdeals.security;

import com.halildurmus.hotdeals.user.User;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

  /**
   * Gets the authenticated {@code User} if there is one.
   *
   * @return {@code User} if there is an authenticated user; otherwise {@code null}
   */
  public User getUser() {
    final SecurityContext securityContext = SecurityContextHolder.getContext();
    final Object principal = securityContext.getAuthentication().getPrincipal();
    if (principal instanceof User) {
      return ((User) principal);
    }

    return null;
  }

}