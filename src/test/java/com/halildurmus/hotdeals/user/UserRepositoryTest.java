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
  private UserRepository repository;

  @AfterEach
  void cleanUp() {
    this.repository.deleteAll();
  }

  @Test
  void findByUidFindsUser() {
    final User user1 = DummyUsers.user1;
    this.repository.save(user1);
    final Optional<User> user = repository.findByUid(user1.getUid());

    assertTrue(user.isPresent());
    assertEquals(user.get().getUid(), user1.getUid());
  }

  @Test
  void findByUidCannotFindUserDueToNonexistentUid() {
    final Optional<User> user = repository.findByUid(DummyUsers.user1.getUid());

    assertTrue(user.isEmpty());
  }

  @Test
  void findByEmailFindsUser() {
    final User user1 = DummyUsers.user1;
    this.repository.save(user1);
    final Optional<User> user = repository.findByEmail(user1.getEmail());

    assertTrue(user.isPresent());
    assertEquals(user.get().getEmail(), user1.getEmail());
  }

  @Test
  void findByEmailCannotFindUserDueToNonexistentEmail() {
    final Optional<User> user = repository.findByEmail(DummyUsers.user1.getEmail());

    assertTrue(user.isEmpty());
  }

  @Test
  void findByNicknameFindsUser() {
    final User user1 = DummyUsers.user1;
    this.repository.save(user1);
    final Optional<User> user = repository.findByNickname(user1.getNickname());

    assertTrue(user.isPresent());
    assertEquals(user.get().getNickname(), user1.getNickname());
  }

  @Test
  void findByNicknameCannotFindUserDueToNonexistentNickname() {
    final Optional<User> user = repository.findByNickname(DummyUsers.user3.getNickname());

    assertTrue(user.isEmpty());
  }

}
