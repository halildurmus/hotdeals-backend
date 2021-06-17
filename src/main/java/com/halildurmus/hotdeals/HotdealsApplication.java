package com.halildurmus.hotdeals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HotdealsApplication {

  public static void main(String[] args) { SpringApplication.run(HotdealsApplication.class, args); }

}
