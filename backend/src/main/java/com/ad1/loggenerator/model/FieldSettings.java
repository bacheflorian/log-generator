package com.ad1.loggenerator.model;

import com.ad1.loggenerator.model.fieldsettingsmodels.*;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldSettings {

    // field settings
    @Valid
    private TimeStamp timeStamp;
    @Valid
    private ProcessingTime processingTime;
    @Valid
    private CurrentUserId currentUserID;
    @Valid
    private BusinessGuid businessGUID;
    @Valid
    private PathToFile pathToFile;
    @Valid
    private FileSha256 fileSHA256;
    @Valid
    private Disposition disposition;
}
