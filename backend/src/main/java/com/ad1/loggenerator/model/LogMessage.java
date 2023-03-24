package com.ad1.loggenerator.model;

import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogMessage {

    private JobStatus status;
    private int logLineCount;
    private long timeStamp;
    private URL url;

}
