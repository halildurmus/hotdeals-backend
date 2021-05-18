package com.halildurmus.hotdeals.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Slf4j
public class ObjectIdArrayJsonSerializer extends JsonSerializer<List<ObjectId>> {

  @Override
  public void serialize(List<ObjectId> objectIds, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) {
    try {
      List<String> ids = new ArrayList<>();
      for (ObjectId id : (objectIds)) {
        ids.add(id.toString());
      }

      jsonGenerator.writeObject(ids);
    } catch (IOException e) {
      log.error("Object ID not serialized", e);
    }
  }

}