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
import com.ad1.loggenerator.model.LogMessage;
import com.ad1.loggenerator.model.SelectionModel;

import lombok.Data;

/**
 * Contains logic for processing user requests and generating outputs specific
 * to the user defined parameters
 */
@Data
@Service
public class StreamingService {

    private LogService logService;
    private boolean continueStreaming; // starts and stops streaming
    private SimpMessagingTemplate template;
    private final int sendStreamDataFrequency = 10000;

    public StreamingService(@Autowired LogService logService, @Autowired SimpMessagingTemplate template) {
        this.logService = logService;
        this.template = template;
    }

    /**
     * Generates streaming log lines until it is told to stop
     * 
     * @param selectionModel defines all the parameters to be included in the stream
     *                       log lines as per the user
     * @return
     */
    @Async("asyncTaskExecutor")
    public void streamMode(SelectionModel selectionModel) {
        try {
            // include logic here to swap between streaming to location vs file as per
            // selectionModel parameters

            String jobId = selectionModel.getJobId();
            int logLineCount = 0;

            // create currentTimeDate as a String to append to filepath
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = currentDateTime.format(formatDateTime);

            // specify filepath location for stream file
            String filename = "C:\\log-generator\\stream\\" + timestamp + ".json";
            FileWriter fileWriter = new FileWriter(filename);

            while (isContinueStreaming()) {
                JSONObject logLine = logService.generateLogLine(selectionModel);
                for (int j = 0; j < selectionModel.getRepeatingLoglinesPercent() - 1; j++) { // repeated for specified
                                                                                             // number of repeated lines
                                                                                             // size
                    fileWriter.write(logLine.toString() + "\n");
                    logLineCount++;
                    if (logLineCount % sendStreamDataFrequency == 0) {
                        sendStreamData(jobId, logLineCount, System.currentTimeMillis() / 1000);
                    }
                }
                fileWriter.write(logLine.toString() + "\n");
                logLineCount++;
                if (logLineCount % sendStreamDataFrequency == 0) {
                    sendStreamData(jobId, logLineCount, System.currentTimeMillis() / 1000);
                }
            }
            fileWriter.close();

        } catch (IOException e) {
            throw new FilePathNotFoundException(e.getMessage());
        }
    }

    /**
     * Sends log data for a stream job to the specified jobId
     */
    public void sendStreamData(String jobId, int logLineCount, long timeStamp) {
        String destination = "/topic/stream/" + jobId;
        LogMessage message = new LogMessage(logLineCount, timeStamp);
        template.convertAndSend(destination, message);
    }

    public String generateJobId() {
        return UUID.randomUUID().toString();
    }
}