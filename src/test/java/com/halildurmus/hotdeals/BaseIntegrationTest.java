package com.halildurmus.hotdeals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@ActiveProfiles("integration-test")
public abstract class BaseIntegrationTest {

  // See https://stackoverflow.com/questions/53514532/
  protected <T> T asParsedJson(Object object) throws JsonProcessingException {
    String json = new ObjectMapper().writeValueAsString(object);

    return JsonPath.read(json, "$");
  }

  private static final int REDIS_PORT = 6379;
  private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:latest");
  private static final GenericContainer<?> redis;

  static {
    redis = new GenericContainer<>(REDIS_IMAGE).withExposedPorts(REDIS_PORT);
    redis.start();
  }

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.redis.host", redis::getContainerIpAddress);
    registry.add("spring.redis.port", () -> redis.getMappedPort(REDIS_PORT));
  }
}