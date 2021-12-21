package com.halildurmus.hotdeals.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Note {

  private String title;

  private String titleLocKey;

  private String body;

  private String bodyLocKey;

  private String image;

  private List<String> titleLocArgs = new ArrayList<>();

  private List<String> bodyLocArgs = new ArrayList<>();

  @NotNull
  private Map<String, String> data;

  @NotEmpty
  private List<String> tokens;

}