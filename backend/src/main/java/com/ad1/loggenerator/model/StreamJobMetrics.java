package com.ad1.loggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * Flag representing if the job has completed
     */
    private boolean isCompleted;
    
}
