package com.halildurmus.hotdeals.deal.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class StringFacet {

  @Field(type = FieldType.Keyword)
  private String facetName;

  @Field(type = FieldType.Keyword)
  private String facetValue;

}