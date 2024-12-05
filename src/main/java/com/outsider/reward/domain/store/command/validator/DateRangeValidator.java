package com.outsider.reward.domain.store.command.validator;

import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, CreateStoreMissionRequest> {

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
    }

    @Override
    public boolean isValid(CreateStoreMissionRequest request, ConstraintValidatorContext context) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            return true; // Let @NotNull handle null validation
        }
        return request.getEndDate().isAfter(request.getStartDate()) || 
               request.getEndDate().isEqual(request.getStartDate());
    }
}
