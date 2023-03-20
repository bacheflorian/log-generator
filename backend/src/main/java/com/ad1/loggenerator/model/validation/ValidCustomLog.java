package com.ad1.loggenerator.model.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = CustomLogValidator.class)
@Target( {ElementType.FIELD} )
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCustomLog {
    
    // define default error message
    public String message() default "Logs must have same fields" + 
                                        " and frequency must sum to 1.0.";

    // define default groups
    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}
