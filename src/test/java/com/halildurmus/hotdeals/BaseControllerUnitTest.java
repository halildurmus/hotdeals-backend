package com.halildurmus.hotdeals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(useDefaultFilters = false, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureJsonTesters
public abstract class BaseControllerUnitTest {

  // See https://stackoverflow.com/questions/53514532/
  protected <T> T asParsedJson(Object object) throws JsonProcessingException {
    var json = new ObjectMapper().writeValueAsString(object);
    return JsonPath.read(json, "$");
  }
}
