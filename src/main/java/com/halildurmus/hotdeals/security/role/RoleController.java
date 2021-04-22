package com.halildurmus.hotdeals.security.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
@IsSuper
public class RoleController {

  @Autowired
  private RoleService roleService;

  @PutMapping
  @ResponseStatus(value = HttpStatus.CREATED)
  public void addRole(@RequestParam String uid, @RequestParam String role) throws Exception {
    roleService.addRole(uid, role);
  }

  @DeleteMapping
  public void removeRole(@RequestParam String uid, @RequestParam String role) {
    roleService.removeRole(uid, role);
  }

}