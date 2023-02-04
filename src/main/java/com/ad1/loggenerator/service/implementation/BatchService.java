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

    private SimpMessagingTemplate template;
    private LogService logService;
    private final int sendBatchDataFrequency = 10000;

    public BatchService(@Autowired SimpMessagingTemplate template, @Autowired LogService logService) {
        this.template = template;
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
    public void batchMode(SelectionModel selectionModel) {
        try {
            // batch settings
            BatchSettings batchSettings = selectionModel.getBatchSettings();

            String jobId = selectionModel.getJobId();
            int logLineCount = 0;

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
                for (int j = 0; j < selectionModel.getRepeatingLoglinesPercent() - 1
                        && i < batchSettings.getNumberOfLogs() - 1; j++, i++) { // repeated for specified number of
                                                                                // repeated lines size
                    fileWriter.write(logLine.toString() + "\n");
                    logLineCount++;
                    if (logLineCount % sendBatchDataFrequency == 0) {
                        sendBatchData(jobId, logLineCount, System.currentTimeMillis() / 1000);
                    }
                }
                fileWriter.write(logLine.toString() + "\n");
                logLineCount++;
                if (logLineCount % sendBatchDataFrequency == 0) {
                    sendBatchData(jobId, logLineCount, System.currentTimeMillis() / 1000);
                }
            }
            fileWriter.close();

        } catch (IOException e) {
            throw new FilePathNotFoundException(e.getMessage());
        }
        
    }

    /**
     * Sends log data for a batch job to the specified clientId
     */
    public void sendBatchData(String jobId, int logLineCount, long timeStamp) {
        String destination = "/topic/batch/" + jobId;
        LogMessage message = new LogMessage(logLineCount, timeStamp);
        template.convertAndSend(destination, message);
    }

    public String generateJobId() {
        return UUID.randomUUID().toString();
    }

}