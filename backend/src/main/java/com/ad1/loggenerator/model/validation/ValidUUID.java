package com.ad1.loggenerator.model.validation;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = UUIDValidator.class)
@Target( {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUUID {
    
    // define default error message
    public String message() default "Must provide a valid UUID.";

    // define default groups
    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}
