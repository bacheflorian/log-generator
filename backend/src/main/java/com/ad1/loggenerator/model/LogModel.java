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
    private String userId;
    private String businessId;
    private String filepath;
    private String fileSHA256;
    private int disposition;

}
