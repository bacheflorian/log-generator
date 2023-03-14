package com.ad1.loggenerator.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectionModel {

    // unique job id for the front end
    private String jobId;
    
    // percent chance a logline should repeat, expressed as a decimal
    private double repeatingLoglinesPercent;

    // field settings
    @Valid
    private FieldSettings fieldSettings;

    // malware settings
    private MalwareSettings malwareSettings;

    // mode selection
    private String mode;

    // stream mode settings
    private StreamSettings streamSettings;

    // batch mode settings
    private BatchSettings batchSettings;

}
