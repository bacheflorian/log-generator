package com.ad1.loggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldSettings {
    // included fields
    private boolean includeTimeStamp;
    private boolean includeProcessingTime;
    private boolean includeCurrentUserID;
    private boolean includeBusinessGUID;
    private boolean includePathToFile;
    private boolean includeFileSHA256;
    private boolean includeDisposition;
}
