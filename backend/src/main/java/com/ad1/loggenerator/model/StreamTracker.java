package com.ad1.loggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamTracker {
    
    // id of the stream job
    private String jobId;
    // count of log lines generated in stream job
    private int logCount;
    // time of the last ping from client in seconds
    private long lastPing;
    // flag to continue streaming or not
    private Boolean continueStreaming;
    // the start time of the stream job in seconds
    private long startTime;
    // the end time of the stream job in seconds
    private long endTime;
    //URL of stream object created in aws s3
    public URL getStreamObjectURL;
}
