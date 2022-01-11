package com.halildurmus.hotdeals.security.role;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.halildurmus.hotdeals.BaseControllerUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Import(RoleController.class)
public class RoleControllerTest extends BaseControllerUnitTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private RoleService service;

  @Test
  @DisplayName("PUT /roles (success)")
  public void addsRole() throws Exception {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("uid", "23hsdfds532h4j32");
    params.add("role", "ROLE_SUPER");
    final RequestBuilder request = put("/roles").params(params);
    mvc.perform(request).andExpect(status().isOk());
  }

  @Test
  @DisplayName("PUT /roles (invalid role enum)")
  public void putRoleValidationFailsDueToInvalidRoleEnum() throws Exception {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("uid", "23hsdfds532h4j32");
    params.add("role", "INVALID_ROLE");
    final RequestBuilder request = put("/roles").params(params);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(status().reason(
            equalTo("Invalid role! Supported roles => [ROLE_ADMIN, ROLE_SUPER, ROLE_MODERATOR]")));
  }

  @Test
  @DisplayName("PUT /roles (no params)")
  public void putRoleValidationFailsDueToNoParams() throws Exception {
    final RequestBuilder request = put("/roles");
    mvc.perform(request).andExpect(status().isBadRequest());
  }

}
