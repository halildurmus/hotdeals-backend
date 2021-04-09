package com.halildurmus.hotdeals.util;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

@Component
public class FakerUtil {
  public String generateNickname() {
    Faker faker = new Faker();
    String prefix = faker.superhero().prefix();
    String firstName = faker.name().firstName();
    String buildingNumber = faker.address().buildingNumber();

    return prefix + firstName + buildingNumber;
  }
}
