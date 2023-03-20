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
public class FileSha256 {

    // whether to include the field
    @NotNull(message="is required")
    private Boolean include;
    // the optional values to include
    @NotNull
    @ValidUUID
    private List<String> values;

}
