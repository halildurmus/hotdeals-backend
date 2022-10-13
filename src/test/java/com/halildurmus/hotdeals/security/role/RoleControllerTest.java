package com.halildurmus.hotdeals.security.role;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.halildurmus.hotdeals.BaseControllerUnitTest;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Import(RoleController.class)
public class RoleControllerTest extends BaseControllerUnitTest {

  @Autowired private MockMvc mvc;

  @MockBean private RoleService roleService;

  @Test
  @DisplayName("PUT /roles (success)")
  public void addsRole() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("uid", "23hsdfds532h4j32");
    params.add("role", Role.ROLE_SUPER.name());
    var request = put("/roles").params(params);
    mvc.perform(request).andExpect(status().isOk());
  }

  @Test
  @DisplayName("PUT /roles (invalid role enum)")
  public void putRoleValidationFailsDueToInvalidRoleEnum() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("uid", "23hsdfds532h4j32");
    params.add("role", "INVALID_ROLE");
    var request = put("/roles").params(params);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains(
                            "Failed to convert value of type 'java.lang.String' to required type 'com.halildurmus.hotdeals.security.role.Role'")));
  }

  @Test
  @DisplayName("PUT /roles (no params)")
  public void putRoleValidationFailsDueToNoParams() throws Exception {
    var request = put("/roles");
    mvc.perform(request).andExpect(status().isBadRequest());
  }
}
