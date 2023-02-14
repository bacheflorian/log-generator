package com.ad1.loggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchJobMetrics {
    
    /**
     * The id of the batch job
     */
    private String jobId;
    /**
     * The number of logs generated
     */
    private int logCount;
    /**
     * The start time of the batch job in seconds
     */
    private long startTime;
    /**
     * The running time of the batch job in seconds
     */
    private long runTime;
    /**
     * The end time of the batch job in seconds
     */
    private Long endTime;
    /**
     * The total batch size
     */
    private int batchSize;
    /**
     * Flag representing if th job has completed
     */
    private boolean isCompleted;

}
