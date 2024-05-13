package org.wolt.woltproject.config.myannotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Under18Validator.class)
public @interface Under18 {
    String message() default "User must be 18 years old!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
