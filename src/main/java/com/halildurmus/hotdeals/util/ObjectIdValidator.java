package com.halildurmus.hotdeals.util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.bson.types.ObjectId;

public class ObjectIdValidator implements ConstraintValidator<ObjectIdConstraint, String> {

  @Override
  public void initialize(ObjectIdConstraint objectId) {
  }

  @Override
  public boolean isValid(String objectId, ConstraintValidatorContext cxt) {
    return ObjectId.isValid(objectId);
  }

}