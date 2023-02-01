package com.ad1.loggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectionModel {

    // repeated lines
    private int repeatedLines = 10; // default unless otherwise specified

    // batch file size
    private int batchSize = 4000; // default unless otherwise specified

    // included fields
    private boolean timeStamp;
    private boolean processingTime;
    private boolean userId;
    private boolean businessId;
    private boolean filepath;
    private boolean fileSHA256;
    private boolean disposition;

//    // include malware
//    private boolean trojan;
//    private boolean adware;
//    private boolean ransom;

    // mode selection
    private String modeSelection;

//    // save logs
//    private boolean saveLogs;
//
//    // stream address
//    private String streamAddress;

}
