package com.halildurmus.hotdeals.user;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "users", exported = false, path = "users")
public interface UserRepository extends MongoRepository<User, String> {

  @Override
  @Caching(
      evict = {
        @CacheEvict(value = "users:findByEmail", key = "#entity.email"),
        @CacheEvict(value = "users:findByNickname", key = "#entity.nickname"),
        @CacheEvict(value = "users:findByUid", key = "#entity.uid")
      })
  <S extends User> S save(S entity);

  @Cacheable(
      value = "users:findByEmail",
      key = "#email",
      condition = "#email.blank != true and #result != null")
  Optional<User> findByEmail(String email);

  @Cacheable(
      value = "users:findByNickname",
      key = "#nickname",
      condition = "#nickname.blank != true and #result != null")
  Optional<User> findByNickname(String nickname);

  @Cacheable(
      value = "users:findByUid",
      key = "#uid",
      condition = "#uid.blank != true and #result != null")
  Optional<User> findByUid(String uid);

  Page<User> findAllByIdIn(Iterable<String> userIds, Pageable pageable);
}
