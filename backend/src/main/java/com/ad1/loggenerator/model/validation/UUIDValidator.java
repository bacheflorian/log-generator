package com.ad1.loggenerator.model.validation;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UUIDValidator implements ConstraintValidator<ValidUUID, List<String>> {

    @Override
    public boolean isValid(List<String> values, ConstraintValidatorContext context) {
        
        // define regex for a uuid
        Pattern pattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        Matcher matcher;

        for (String value: values) {
            matcher = pattern.matcher(value);
            boolean matchFound = matcher.find();

            // if uuid pattern is not found, return false
            if (!matchFound) {
                return false;
            }
        }

        return true;
    }
    
    
}
