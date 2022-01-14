package com.halildurmus.hotdeals.security.role;

public interface RoleService {

  void add(String uid, Role role);

  void delete(String uid, Role role);

}