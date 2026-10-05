// src/main/java/com/hirevo/common/validation/RangesValidator.java
package com.hirevo.common.validation;

import com.hirevo.job.dto.CreateJobRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// "Checks the @ValidRanges label when it sits on a CreateJobRequest"
public class RangesValidator implements ConstraintValidator<ValidRanges, CreateJobRequest> {

    @Override
    public boolean isValid(CreateJobRequest r, ConstraintValidatorContext ctx) {
        if (r == null) return true;   // an empty request is other rules' job

        // Salaries are optional, so only compare when BOTH were given
        boolean salaryOk = r.salaryMin() == null || r.salaryMax() == null
                || r.salaryMin() <= r.salaryMax();
        boolean experienceOk = r.minExperience() <= r.maxExperience();

        return salaryOk && experienceOk;   // false → a validation error
    }
}