package com.halildurmus.hotdeals.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

@DataMongoTest
@ActiveProfiles("integration-test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @AfterEach
  void cleanUp() {
    this.userRepository.deleteAll();
  }

  @Test
  void findByUidShouldReturnUser() {
    this.userRepository.save(DummyUsers.user1);

    Optional<User> user = userRepository.findByUid(DummyUsers.user1.getUid());

    assertTrue(user.isPresent());
    assertEquals(user.get().getUid(), DummyUsers.user1.getUid());
  }

  @Test
  void findByUidShouldNotReturnUserWhenUidIsInvalid() {
    Optional<User> user = userRepository.findByUid(DummyUsers.user1.getUid());

    assertTrue(user.isEmpty());
  }

  @Test
  void findByEmailShouldReturnUser() {
    this.userRepository.save(DummyUsers.user2);

    Optional<User> user = userRepository.findByEmail(DummyUsers.user2.getEmail());

    assertTrue(user.isPresent());
    assertEquals(user.get().getEmail(), DummyUsers.user2.getEmail());
  }

  @Test
  void findByEmailShouldNotReturnUserWhenEmailIsInvalid() {
    Optional<User> user = userRepository.findByEmail(DummyUsers.user2.getEmail());

    assertTrue(user.isEmpty());
  }

  @Test
  void findByNicknameShouldReturnUser() {
    this.userRepository.save(DummyUsers.user3);

    Optional<User> user = userRepository.findByNickname(DummyUsers.user3.getNickname());

    assertTrue(user.isPresent());
    assertEquals(user.get().getNickname(), DummyUsers.user3.getNickname());
  }

  @Test
  void findByNicknameShouldNotReturnUserWhenNicknameIsInvalid() {
    Optional<User> user = userRepository.findByNickname(DummyUsers.user3.getNickname());

    assertTrue(user.isEmpty());
  }

}
