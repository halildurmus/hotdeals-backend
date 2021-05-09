package com.halildurmus.hotdeals.security.role;

public interface RoleService {

  void addRole(String uid, String role) throws Exception;

  void removeRole(String uid, String role);

}