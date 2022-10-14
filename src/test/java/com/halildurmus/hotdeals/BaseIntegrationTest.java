package com.halildurmus.hotdeals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("integration-test")
@AutoConfigureJsonTesters
@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseIntegrationTest {

  // See https://stackoverflow.com/questions/53514532/
  protected <T> T asParsedJson(Object object) throws JsonProcessingException {
    var json = new ObjectMapper().writeValueAsString(object);
    return JsonPath.read(json, "$");
  }
}
