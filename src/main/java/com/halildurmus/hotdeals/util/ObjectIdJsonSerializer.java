package com.halildurmus.hotdeals.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Slf4j
public class ObjectIdJsonSerializer extends JsonSerializer<ObjectId> {

  @Override
  public void serialize(ObjectId objectId, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) {
    try {
      jsonGenerator.writeString(objectId.toString());
    } catch (IOException e) {
      log.error("Failed to serialize ObjectId!", e);
    }
  }

}