package com.ad1.loggenerator.service.implementation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ad1.loggenerator.model.LogMessage;
import com.ad1.loggenerator.model.StreamTracker;

import lombok.Data;

/**
 * Contains logic for checking whether to stop a stream job
 * and to send stream data for each stream job
 */
@Data
@Service
public class StreamServiceTracker {

    /**
     * Seconds before timing out a stream job
     */
    private final long secondsTimeOut = 60;
    /**
     * Milliseconds to wait between sending data to frontend
     */
    private final long millsecondsPerMessage = 1000;
    @Autowired
    private SimpMessagingTemplate template;
    /**
     * HashMap of all stream jobs
     */
    private Map<String, StreamTracker> jobsList = new ConcurrentHashMap<String, StreamTracker>();

    /**
     * Sends stream data to the front end, checks every stream job
     * and sets continueStreaming to false if lastPing+5s < current time.
     * Then removes all stream jobs with continueStreaming set to false
     */
    @Async("asyncTaskExecutor")
    public void checkLastPings() throws InterruptedException {

        if (jobsList.size() == 0) {
            return;
        }

        while (jobsList.size() > 0) {
            Thread.sleep(millsecondsPerMessage);

            for (String jobId : jobsList.keySet()) {
                StreamTracker job = jobsList.get(jobId);

                sendStreamData(job);

                if (job.getLastPing() + secondsTimeOut < System.currentTimeMillis() / 1000) {
                    job.setContinueStreaming(false);
                }

                if (!job.getContinueStreaming()) {
                    jobsList.remove(jobId);
                }
            }
        }

    }

    /**
     * Sends log data for a stream job
     * 
     * @throws InterruptedException
     */
    public void sendStreamData(StreamTracker job) throws InterruptedException {

        if (jobsList.size() == 0) {
            return;
        }

        String destination = "/topic/job";
        LogMessage message = new LogMessage();

        if (job != null) {
            message.setLogLineCount(job.getLogCount());
            message.setTimeStamp(System.currentTimeMillis() / 1000);

            template.convertAndSend(destination + "/" + job.getJobId(), message);
        }
    }

    /**
     *
     * Adds a new stream job to the jobs list
     * 
     * @param streamTracker the new stream job tracker
     */
    public void addNewJob(StreamTracker streamTracker) {
        jobsList.put(streamTracker.getJobId(), streamTracker);
    }

    /**
     * Returns the number of jobs
     * 
     * @return the number of jobs
     */
    public int getJobsListSize() {
        return jobsList.size();
    }

    /**
     * Stop a stream job right away
     * 
     * @param jobId
     * @return
     */
    public boolean stopStreamJob(String jobId) {
        StreamTracker streamTracker = jobsList.get(jobId);
        if (streamTracker == null) {
            return false;
        }

        streamTracker.setContinueStreaming(false);
        return true;
    }

    /**
     * Update the lastPing to continue a stream job
     */
    public boolean continueStreamJob(String jobId) {
        StreamTracker streamTracker = jobsList.get(jobId);
        if (streamTracker == null) {
            return false;
        }

        streamTracker.setLastPing(System.currentTimeMillis() / 1000);
        return true;
    }
}
