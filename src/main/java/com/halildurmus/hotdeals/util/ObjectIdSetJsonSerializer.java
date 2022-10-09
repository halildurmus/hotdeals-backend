package com.halildurmus.hotdeals.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Slf4j
public class ObjectIdSetJsonSerializer extends JsonSerializer<Set<ObjectId>> {

  @Override
  public void serialize(
      Set<ObjectId> objectIds, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
    try {
      Set<String> ids = new HashSet<>();
      objectIds.forEach(objectId -> ids.add(objectId.toString()));
      jsonGenerator.writeObject(ids);
    } catch (IOException e) {
      log.error("Failed to serialize Set<ObjectId>!", e);
    }
  }
}
