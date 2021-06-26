package com.halildurmus.hotdeals.notification;

import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Note {

  private String title;

  private String body;

  private String image;

  private Map<String, String> data;

}