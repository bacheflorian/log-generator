package com.ad1.loggenerator.service.implementation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ad1.loggenerator.model.BatchTracker;
import com.ad1.loggenerator.model.LogMessage;

import lombok.Data;

/**
 * Contains logic to send stream data for each batch job
 */
@Data
@Service
public class BatchServiceTracker {
    
    @Autowired
    private SimpMessagingTemplate template;
    /**
     * HashMap of all batch jobs
     */
    private Map<String, BatchTracker> jobsList = new ConcurrentHashMap<String, BatchTracker>();

    /**
     * Sends log data for every batch job in the jobs list. Then removes batch jobs
     * that have finished processing
     */
    @Async("asyncTaskExecutor")
    public void sendBatchData() throws InterruptedException {
        if (jobsList.size() == 0) {
            return;
        }

        BatchTracker job = null;
        String destination = "/topic/batch";
        LogMessage message = new LogMessage();

        while (jobsList.size() > 0) {
            Thread.sleep(1000);
            
            for (String jobId : jobsList.keySet()) {
                job = jobsList.get(jobId);

                message.setLogLineCount(job.getLogCount());
                message.setTimeStamp(System.currentTimeMillis() / 1000);
                template.convertAndSend(destination + "/" + jobId, message);
            }

            for (String jobId : jobsList.keySet()) {
                job = jobsList.get(jobId);

                if (job.getLogCount() >= job.getBatchSize()) {
                    jobsList.remove(jobId);
                }
            }
        }
    }

    /**
     * Add a new batch job to the jobs list
     * @param batchTracker the new batch job tracker
     */
    public void addNewJob(BatchTracker batchTracker) {
        jobsList.put(batchTracker.getJobId(), batchTracker);
    }
    
    /**
     * Return the number of batch jobs
     * @return the number of batch jobs
     */
    public int getJobsListSize() {
        return jobsList.size();
    }

}
