package com.halildurmus.hotdeals.security.role;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
@IsSuper
public class RoleController {

  @Autowired
  private RoleService service;

  private boolean isInvalidRole(String value) {
    return Arrays.stream(Role.class.getEnumConstants()).noneMatch(e -> e.name().equals(value));
  }

  @PutMapping
  public void addRole(@RequestParam String uid, @RequestParam String role) throws Exception {
    if (isInvalidRole(role)) {
      throw new Exception("Invalid role! Allowed roles => " + Arrays.toString(Role.values()));
    }

    service.addRole(uid, role);
  }

  @DeleteMapping
  public void removeRole(@RequestParam String uid, @RequestParam String role) throws Exception {
    if (isInvalidRole(role)) {
      throw new Exception("Invalid role! Allowed roles => " + Arrays.toString(Role.values()));
    }

    service.removeRole(uid, role);
  }

}