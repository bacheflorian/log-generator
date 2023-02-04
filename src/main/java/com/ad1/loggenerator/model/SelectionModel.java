package com.ad1.loggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectionModel {

    // unique client id for the front end
    private int clientId;
    
    // percent chance a logline should repeat, expressed as a decimal
    private int repeatingLoglinesPercent = 0; // default unless otherwise specified

    // field settings
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
