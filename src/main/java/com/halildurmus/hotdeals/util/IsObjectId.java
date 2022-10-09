package com.halildurmus.hotdeals.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;

/**
 * Validates if annotated field or parameter is {@code ObjectId}.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ObjectIdValidator.class)
@NotNull
@Documented
public @interface IsObjectId {

  String message() default "Invalid ObjectId";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}