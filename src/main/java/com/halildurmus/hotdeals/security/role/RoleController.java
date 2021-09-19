package com.halildurmus.hotdeals.security.role;

import com.halildurmus.hotdeals.security.models.SecurityProperties;
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
  private RoleService roleService;

  @Autowired
  private SecurityProperties securityProps;

  @PutMapping
  public void addRole(@RequestParam String uid, @RequestParam String role) throws Exception {
    if (!securityProps.getValidApplicationRoles().contains(role)) {
      throw new Exception("Not a valid Application role, Allowed roles => "
          + securityProps.getValidApplicationRoles().toString());
    }

    roleService.addRole(uid, role);
  }

  @DeleteMapping
  public void removeRole(@RequestParam String uid, @RequestParam String role) throws Exception {
    if (!securityProps.getValidApplicationRoles().contains(role)) {
      throw new Exception("Not a valid Application role, Allowed roles => "
          + securityProps.getValidApplicationRoles().toString());
    }

    roleService.removeRole(uid, role);
  }

}