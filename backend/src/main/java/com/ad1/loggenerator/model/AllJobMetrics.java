package com.ad1.loggenerator.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllJobMetrics {
    
    /**
     * The number of active stream jobs
     */
    private int numActiveStreamJobs;
    /**
     * The number of completed and active stream jobs
     */
    private int numAllStreamJobs;
    /**
     * The number of active batch jobs
     */
    private int numActiveBatchJobs;
    /**
     * The number of active and completed batch jobs
     */
    private int numAllBatchJobs;
    /**
     * List of metrics for all completed and active batch jobs
     */
    private List<BatchJobMetrics> batchJobs = new ArrayList<BatchJobMetrics>();
    /**
     * List of metrics for all completed and active stream jobs
     */
    private List<StreamJobMetrics> streamJobs = new ArrayList<StreamJobMetrics>();
    
}
