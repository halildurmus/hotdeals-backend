package com.halildurmus.hotdeals.user;

import com.halildurmus.hotdeals.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RepositoryRestController
public class UserController {

  @Autowired
  private UserService service;

  @Autowired
  private SecurityService securityService;

  @PostMapping("/users")
  public ResponseEntity<User> createUser(@RequestBody User user) {
    User response = service.create(user);

    return ResponseEntity.status(201).body(response);
  }

  @GetMapping("/users/me")
  public ResponseEntity<User> getAuthenticatedUser() {
    User response = securityService.getUser();

    return ResponseEntity.status(200).body(response);
  }

}