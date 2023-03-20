package com.ad1.loggenerator.model.validation;

import java.util.List;
import java.util.Set;

import com.ad1.loggenerator.model.CustomLog;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomLogValidator implements ConstraintValidator<ValidCustomLog, List<CustomLog>> {
    
    @Override
    public boolean isValid(List<CustomLog> values, ConstraintValidatorContext context) {

        if (values == null || values.size() == 0) {
            return true;
        }

        // Check that the frequencies sum up to 1
        double total = 0;
        for (CustomLog customLog: values) {
            total += customLog.getFrequency();
        }

        if (total > 1.0000000) {
            return false;
        }

        // check that the keys are the same for each custom log
        Set<String> requiredFieldNames = values.get(0).getFields().keySet();

        for (CustomLog customLog: values) {
            Set<String> fieldNames = customLog.getFields().keySet();

            if (!requiredFieldNames.equals(fieldNames)) {
                return false;
            }
        }

        return true;
    }

}
