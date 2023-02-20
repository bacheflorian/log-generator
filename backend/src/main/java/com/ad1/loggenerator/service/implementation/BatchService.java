package com.ad1.loggenerator.service.implementation;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ad1.loggenerator.exception.FilePathNotFoundException;
import com.ad1.loggenerator.model.BatchSettings;
import com.ad1.loggenerator.model.BatchTracker;
import com.ad1.loggenerator.model.JobStatus;
import com.ad1.loggenerator.model.LogMessage;
import com.ad1.loggenerator.model.SelectionModel;

import lombok.Data;

/**
 * Contains logic for processing user requests and generating outputs specific
 * to the user defined parameters
 */
@Data
@Service
public class BatchService {

    private LogService logService;

    public BatchService(@Autowired LogService logService) {
        this.logService = logService;
    }

    /**
     * Generates and populates the batch file
     * 
     * @param selectionModel defines all the parameters to be included in the batch
     *                       files as per the user
     * @return
     */
    @Async("asyncTaskExecutor")
    public void batchMode(SelectionModel selectionModel, BatchTracker batchJobTracker) {
        try {
            // batch settings
            BatchSettings batchSettings = selectionModel.getBatchSettings();

            // create currentTimeDate as a String to append to filepath
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = currentDateTime.format(formatDateTime);

            // specify filepath location for batch file
            String filename = "C:\\log-generator\\batch\\" + timestamp + ".json";
            FileWriter fileWriter = new FileWriter(filename);

            // add log lines to batch file
            for (int i = 0; i < batchSettings.getNumberOfLogs(); i++) { // repeat for specified batch size
                JSONObject logLine = logService.generateLogLine(selectionModel);
                fileWriter.write(logLine.toString() + "\n");
                batchJobTracker.setLogCount(batchJobTracker.getLogCount() + 1);

                // determine if a log lines repeats
                if (Math.random() < selectionModel.getRepeatingLoglinesPercent()) {
                    fileWriter.write(logLine.toString() + "\n");
                    i++;
                    batchJobTracker.setLogCount(batchJobTracker.getLogCount() + 1);
                }

            }
                fileWriter.close();
        } catch (IOException e) {
            // Mark the job as failed if an exception occurred
            batchJobTracker.setStatus(JobStatus.FAILED);
            throw new FilePathNotFoundException(e.getMessage());
        }

        // Mark the job as completed if no exception occurred
        if (batchJobTracker.getStatus() == JobStatus.ACTIVE) {
            batchJobTracker.setStatus(JobStatus.COMPLETED);
        }
    }

    public String generateJobId() {
        return UUID.randomUUID().toString();
    }

}