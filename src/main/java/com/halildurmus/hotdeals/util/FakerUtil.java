package com.halildurmus.hotdeals.util;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

/** A utility class with useful function(s) to make it easier to work with {@code Faker}. */
@Component
public class FakerUtil {

  private final Faker faker = new Faker();

  /**
   * Generates a random nickname using {@code Faker}.
   *
   * @return a random nickname
   */
  public String generateNickname() {
    var prefix = faker.superhero().prefix();
    var firstName = faker.name().firstName();
    var digits = faker.number().digits(3);
    return prefix + firstName + digits;
  }
}
