package com.ad1.loggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamJobMetrics {

    /**
     * The id of the stream job
     */
    private String jobId;
    /**
     * The number of logs generated
     */
    private int logCount;
    /**
     * The start time of the stream job in seconds
     */
    private long startTime;
    /**
     * The running time of the stream job in seconds
     */
    private long runTime;
    /**
     * The end time of the stream job in seconds
     */
    private Long endTime;
    /**
     * The current status of the batch job
     */
    private JobStatus status;

    // URL for a particular stream job
    private URL streamObjectURL;
    
}
