package com.ad1.loggenerator.model;

import com.ad1.loggenerator.model.fieldsettingsmodels.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldSettings {

    // field settings
    private TimeStamp timeStamp;
    private ProcessingTime processingTime;
    private CurrentUserId currentUserID;
    private BusinessGuid businessGUID;
    private PathToFile pathToFile;
    private FileSha256 fileSHA256;
    private Disposition disposition;
}
