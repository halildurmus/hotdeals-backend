package com.halildurmus.hotdeals.util;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

@Component
public class FakerUtil {

  private final Faker faker = new Faker();

  public String generateNickname() {
    final String prefix = faker.superhero().prefix();
    final String firstName = faker.name().firstName();
    final String digits = faker.number().digits(2);

    return prefix + firstName + digits;
  }

}
