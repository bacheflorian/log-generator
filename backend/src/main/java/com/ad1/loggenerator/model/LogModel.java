package com.ad1.loggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogModel {

    private long timeStamp;
    private long processingTime;
    private String currentUserID;
    private String businessGUID;
    private String pathToFile;
    private String fileSHA256;
    private int disposition;

}
