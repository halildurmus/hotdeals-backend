package com.halildurmus.hotdeals.deal.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@AllArgsConstructor
public class NumberFacet {

  @Field(type = FieldType.Keyword)
  private String facetName;

  @Field(type = FieldType.Double)
  private Double facetValue;

}