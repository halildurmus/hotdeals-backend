package com.halildurmus.hotdeals.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Note {

  @NotBlank
  private String titleLocKey;

  @NotBlank
  private String bodyLocKey;

  private List<String> titleLocArgs = new ArrayList<>();

  private List<String> bodyLocArgs = new ArrayList<>();

  @NotNull
  private Map<String, String> data;

  @NotEmpty
  private List<String> tokens;

}