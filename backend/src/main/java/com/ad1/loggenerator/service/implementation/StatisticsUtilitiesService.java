package com.ad1.loggenerator.service.implementation;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ad1.loggenerator.model.AllJobMetrics;
import com.ad1.loggenerator.model.BatchJobMetrics;
import com.ad1.loggenerator.model.BatchTracker;
import com.ad1.loggenerator.model.JobStatus;
import com.ad1.loggenerator.model.StreamJobMetrics;
import com.ad1.loggenerator.model.StreamTracker;

import lombok.Data;

@Data
@Service
public class StatisticsUtilitiesService {
    
    @Autowired 
    private BatchTrackerService batchServiceTracker;
    @Autowired
    private StreamTrackerService streamServiceTracker;

    /**
     * Get metrics for all batch and stream jobs
     * @return metrics for all jobs
     */
    public AllJobMetrics generateAllJobMetrics() {
        AllJobMetrics allJobMetrics = new AllJobMetrics();

        allJobMetrics.setNumActiveBatchJobs(batchServiceTracker.getActiveJobsListSize());
        allJobMetrics.setNumActiveStreamJobs(streamServiceTracker.getActiveJobsListSize());
        allJobMetrics.setNumAllBatchJobs(batchServiceTracker.getHistoryJobsListSize());
        allJobMetrics.setNumAllStreamJobs(streamServiceTracker.getHistoryJobsListSize());
        
        ConcurrentHashMap <String, BatchTracker> batchHistoryJobsList = 
            (ConcurrentHashMap<String, BatchTracker>) batchServiceTracker.getHistoryJobsList();
        
        for (String jobId: batchHistoryJobsList.keySet()) {
            BatchJobMetrics batchJobMetrics = generateBatchJobMetrics(jobId);

            allJobMetrics.getBatchJobs().add(batchJobMetrics);
        }

        ConcurrentHashMap <String, StreamTracker> streamHistoryJobsList = 
            (ConcurrentHashMap<String, StreamTracker>) streamServiceTracker.getHistoryJobsList();
        
        for (String jobId: streamHistoryJobsList.keySet()) {
            StreamJobMetrics streamJobMetrics = generateStreamJobMetrics(jobId);

            allJobMetrics.getStreamJobs().add(streamJobMetrics);
        } 

        return allJobMetrics;
    }

    /**
     * Get metrics for a specific batch job
     * @return metrics for a specific batch job
     */
    public BatchJobMetrics generateBatchJobMetrics(String jobId) {
        BatchJobMetrics batchJobMetrics = new BatchJobMetrics();
        // Get the Batch job from batchServiceTracker
        BatchTracker batchJob = batchServiceTracker.getBatchJobTracker(jobId);

        if (batchJob == null) {
            return null;
        }

        batchJobMetrics.setJobId(batchJob.getJobId());
        batchJobMetrics.setLogCount(batchJob.getLogCount());
        batchJobMetrics.setStartTime(batchJob.getStartTime());
        batchJobMetrics.setBatchSize(batchJob.getBatchSize());
        batchJobMetrics.setBatchObjectURL(batchJob.getBatchObjectURL());
        batchJobMetrics.setStatus(batchJob.getStatus());

        if (batchJob.getStatus() != JobStatus.ACTIVE) {
            batchJobMetrics.setEndTime(batchJob.getEndTime());
            batchJobMetrics.setRunTime(
                batchJob.getEndTime() - batchJob.getStartTime());
        } else {
            batchJobMetrics.setEndTime(null);
            batchJobMetrics.setRunTime(
                System.currentTimeMillis() / 1000 - batchJob.getStartTime());
        }
        
        return batchJobMetrics;
    }

    /**
     * Get metrics for a specific stream job
     * @return metrics for a specific stream job
     */
    public StreamJobMetrics generateStreamJobMetrics(String jobId) {
        StreamJobMetrics streamJobMetrics = new StreamJobMetrics();
        // Get the Stream job from streamServiceTracker
        StreamTracker streamJob = streamServiceTracker.getStreamJobTracker(jobId);

        if (streamJob == null) {
            return null;
        }

        streamJobMetrics.setJobId(streamJob.getJobId());
        streamJobMetrics.setLogCount(streamJob.getLogCount());
        streamJobMetrics.setStartTime(streamJob.getStartTime());
        streamJobMetrics.setStreamObjectURL(streamJob.getStreamObjectURL());
        streamJobMetrics.setStatus(streamJob.getStatus());

        if (streamJob.getStatus() != JobStatus.ACTIVE) {
            streamJobMetrics.setEndTime(streamJob.getEndTime());
            streamJobMetrics.setRunTime(streamJob.getEndTime() - streamJob.getStartTime());
        } else {
            streamJobMetrics.setEndTime(null);
            streamJobMetrics.setRunTime(
                System.currentTimeMillis() / 1000 - streamJob.getStartTime()
            );
        }

        return streamJobMetrics;
    }
}
