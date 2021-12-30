package com.halildurmus.hotdeals.deal.es;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class NumberFacet {

  @NotNull
  @Field(type = FieldType.Keyword)
  private final String facetName;

  @NotNull
  @Field(type = FieldType.Double)
  private final Double facetValue;

}