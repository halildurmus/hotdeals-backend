package com.halildurmus.hotdeals.security.role;

@IsSuper
public interface RoleService {

  void add(String uid, Role role);

  void delete(String uid, Role role);
}
