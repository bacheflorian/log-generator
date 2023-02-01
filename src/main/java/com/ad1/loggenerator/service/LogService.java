package com.ad1.loggenerator.service;

import com.ad1.loggenerator.model.LogModel;
import com.ad1.loggenerator.model.SelectionModel;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import lombok.Data;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Contains logic for processing user requests and generating outputs specific to the user defined parameters
 */
@Data
@Service
public class LogService {

    private boolean continueStreaming; // starts and stops streaming

    /**
     * Generates and populates the batch file
     * @param selectionModel defines all the parameters to be included in the batch files as per the user
     * @return
     */
    public String batchMode(SelectionModel selectionModel) {
        try {
            // create currentTimeDate as a String to append to filepath
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = currentDateTime.format(formatDateTime);

            // specify filepath location for batch file
            String filename = "C:\\log-generator\\batch\\" + timestamp + ".json";
            FileWriter fileWriter = new FileWriter(filename);

            // add log lines to batch file
            for (int i = 0; i < selectionModel.getBatchSize(); i++){ // repeat for specified batch size
                JSONObject logLine = generateLogLine(selectionModel);
                for (int j = 0; j < selectionModel.getRepeatedLines() - 1 && i < selectionModel.getBatchSize() - 1; j++, i++) { // repeated for specified number of repeated lines size
                    fileWriter.write(logLine.toString() + "\n");

                }
                fileWriter.write(logLine.toString() + "\n");
            }
            fileWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Successfully generated batch file";
    }

    /**
     * Generates streaming log lines until it is told to stop
     * @param selectionModel defines all the parameters to be included in the stream log lines as per the user
     * @return
     */
    public String streamMode(SelectionModel selectionModel) {
        try {
            // include logic here to swap between streaming to location vs file as per selectionModel parameters

            // create currentTimeDate as a String to append to filepath
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = currentDateTime.format(formatDateTime);

            // specify filepath location for stream file
            String filename = "C:\\log-generator\\stream\\" + timestamp + ".json";
            FileWriter fileWriter = new FileWriter(filename);

            while (isContinueStreaming()) {
                JSONObject logLine = generateLogLine(selectionModel);
                for (int j = 0; j < selectionModel.getRepeatedLines() - 1; j++) { // repeated for specified number of repeated lines size
                    fileWriter.write(logLine.toString() + "\n");
                }
                fileWriter.write(logLine.toString() + "\n");
            }
            fileWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Successfully generated stream file";
    }

    /**
     * Generates each log line as defined by the parameters
     * @param selectionModel defines all the parameters to be included in the log lines as per the user
     * @return a single log line in JSON format
     */
    public JSONObject generateLogLine(SelectionModel selectionModel) {

        // Create logline objects
        LogModel logLine = new LogModel();
        JSONObject logLineJSON = new JSONObject();

        // Generate log line data
        if (selectionModel.isTimeStamp()) {
            logLine.setTimeStamp((long) ((System.currentTimeMillis() / 1000) * Math.random()));
            logLineJSON.put("timeStamp", logLine.getTimeStamp());
        }
        if (selectionModel.isProcessingTime()) {
            logLine.setProcessingTime((long) (Math.random() * 1000));
            logLineJSON.put("processingTime", logLine.getProcessingTime());
        }
        if (selectionModel.isUserId()) {
            logLine.setUserId(UUID.randomUUID());
            logLineJSON.put("userId", logLine.getUserId());
        }
        if (selectionModel.isBusinessId()) {
            logLine.setBusinessId(UUID.randomUUID());
            logLineJSON.put("businessId", logLine.getBusinessId());
        }

//        logLine.setFilepath(); // Requires it's own function

        if (selectionModel.isFileSHA256()) {
            logLine.setFileSHA256(UUID.randomUUID());
            logLineJSON.put("fileSHA256", logLine.getFileSHA256());
        }
        if (selectionModel.isDisposition()) {
            logLine.setDisposition((int) Math.random() * ((4 - 1) + 1) + 1); // 1 = Clean, 2 = Suspicious, 3 = Malicious, 4 = Unknown
            logLineJSON.put("disposition", logLine.getDisposition());
        }

        // additional code required for malware

        return logLineJSON;
    }

}
