package com.ad1.loggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamTracker {
    
    // id of the stream job
    private String jobId;
    // count of log lines generated in stream job
    private int logCount;
    // time of the last ping from client
    private long lastPing;
    private Boolean continueStreaming;

}
