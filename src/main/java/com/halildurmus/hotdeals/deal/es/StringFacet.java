package com.halildurmus.hotdeals.deal.es;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class StringFacet {

  @NotNull
  @Field(type = FieldType.Keyword)
  private final String facetName;

  @NotNull
  @Field(type = FieldType.Keyword)
  private final String facetValue;
}
