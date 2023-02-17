package com.ad1.loggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchTracker {
    // id of the stream job
    private String jobId;
    // count of log lines generated in stream job
    private int logCount;
    // size of the batch requested
    private int batchSize;
    // the start time of the batch job in seconds
    private long startTime;
    // the end time of the batch job in seconds
    private long endTime;
    //URL of batch object created in aws s3
    private URL getBatchObjectURL;
    
}
