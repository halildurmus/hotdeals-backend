package com.halildurmus.hotdeals.util;

import com.github.javafaker.Faker;

public class FakerUtil {
  public static String generateNickname() {
    Faker faker = new Faker();
    String prefix = faker.superhero().prefix();
    String firstName = faker.name().firstName();
    String buildingNumber = faker.address().buildingNumber();

    return prefix + firstName + buildingNumber;
  }
}
