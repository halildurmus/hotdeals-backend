package com.halildurmus.hotdeals.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

@DataMongoTest
@ActiveProfiles("integration-test")
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  @AfterEach
  void cleanUp() {
    this.userRepository.deleteAll();
  }

  @Test
  void findByUidFindsUser() {
    var user1 = DummyUsers.user1;
    this.userRepository.save(user1);
    var user = userRepository.findByUid(user1.getUid());

    assertTrue(user.isPresent());
    assertEquals(user.get().getUid(), user1.getUid());
  }

  @Test
  void findByUidCannotFindUserDueToNonexistentUid() {
    var user = userRepository.findByUid(DummyUsers.user1.getUid());
    assertTrue(user.isEmpty());
  }

  @Test
  void findByEmailFindsUser() {
    var user1 = DummyUsers.user1;
    this.userRepository.save(user1);
    var user = userRepository.findByEmail(user1.getEmail());

    assertTrue(user.isPresent());
    assertEquals(user.get().getEmail(), user1.getEmail());
  }

  @Test
  void findByEmailCannotFindUserDueToNonexistentEmail() {
    var user = userRepository.findByEmail(DummyUsers.user1.getEmail());
    assertTrue(user.isEmpty());
  }

  @Test
  void findByNicknameFindsUser() {
    var user1 = DummyUsers.user1;
    this.userRepository.save(user1);
    var user = userRepository.findByNickname(user1.getNickname());

    assertTrue(user.isPresent());
    assertEquals(user.get().getNickname(), user1.getNickname());
  }

  @Test
  void findByNicknameCannotFindUserDueToNonexistentNickname() {
    var user = userRepository.findByNickname(DummyUsers.user3.getNickname());
    assertTrue(user.isEmpty());
  }
}
