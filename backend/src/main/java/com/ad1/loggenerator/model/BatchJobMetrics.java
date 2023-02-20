package com.ad1.loggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

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
    // URL for a particular batch job
    private URL batchObjectURL;
    /**
     * The current status of the batch job
     */
    private JobStatus status;

}
