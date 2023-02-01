package com.ad1.loggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogModel {

    private long timeStamp;
    private long processingTime;
    private UUID userId;
    private UUID businessId;
    private String filepath = "C:\\corruptedFiles"; // make null once function is built
    private UUID fileSHA256;
    private int disposition;

}
