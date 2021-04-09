package com.halildurmus.hotdeals.user;

import com.halildurmus.hotdeals.util.FakerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

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

}
