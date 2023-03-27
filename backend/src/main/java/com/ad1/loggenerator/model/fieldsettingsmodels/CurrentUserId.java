package com.ad1.loggenerator.model.fieldsettingsmodels;

import java.util.List;

import com.ad1.loggenerator.model.validation.ValidUUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserId {
    
    // whether to include the field
    @NotNull(message="is required")
    private Boolean include;
    // the optional values to include
    // @ValidUUID
    @NotNull
    private List<String> values;
    
}
