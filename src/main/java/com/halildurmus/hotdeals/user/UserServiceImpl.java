package com.halildurmus.hotdeals.user;

import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.util.FakerUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private SecurityService securityService;

  private final UserRepository repository;
  private final FakerUtil fakerUtil;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, FakerUtil fakerUtil) {
    this.repository = userRepository;
    this.fakerUtil = fakerUtil;
  }

  @Override
  public User create(User user) {
    boolean error;

    do {
      try {
        final String nickname = fakerUtil.generateNickname();
        user.setNickname(nickname);
        repository.insert(user);
        error = false;
      } catch (Exception e) {
        log.error(e.getMessage());

        if (e instanceof DuplicateKeyException && e.getMessage().contains("nickname")) {
          error = true;
        } else {
          throw e;
        }
      }
    } while (error);

    return user;
  }

  @Override
  public User block(String userId) throws Exception {
    final User user = securityService.getUser();
    final List<String> blockedUsers = user.getBlockedUsers();

    if (blockedUsers.contains(userId)) {
      throw new Exception("You've already blocked this user before!");
    } else {
      blockedUsers.add(userId);
      user.setBlockedUsers(blockedUsers);
      repository.save(user);
    }

    return user;
  }

  @Override
  public User unblock(String userId) throws Exception {
    final User user = securityService.getUser();
    final List<String> blockedUsers = user.getBlockedUsers();

    if (blockedUsers.contains(userId)) {
      blockedUsers.remove(userId);
      user.setBlockedUsers(blockedUsers);
      repository.save(user);
    } else {
      throw new Exception("You've already unblocked this user before!");
    }

    return user;
  }

}
