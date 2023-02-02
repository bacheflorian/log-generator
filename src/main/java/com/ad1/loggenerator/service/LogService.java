package com.ad1.loggenerator.service;

import com.ad1.loggenerator.model.LogModel;
import com.ad1.loggenerator.model.SelectionModel;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import lombok.Data;

import java.io.File;
import java.util.Random;
import java.util.UUID;

/**
 * Contains logic for generating outputs specific to the user defined parameters
 */
@Data
@Service
public class LogService {

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
            logLine.setTimeStamp(generateTimeStamp());
            logLineJSON.put("timeStamp", logLine.getTimeStamp());
        }
        if (selectionModel.isProcessingTime()) {
            logLine.setProcessingTime(generateProcessingTime());
            logLineJSON.put("processingTime", logLine.getProcessingTime());
        }
        if (selectionModel.isUserId()) {
            logLine.setUserId(generateUserId());
            logLineJSON.put("userId", logLine.getUserId());
        }
        if (selectionModel.isBusinessId()) {
            logLine.setBusinessId(generateBusinessId());
            logLineJSON.put("businessId", logLine.getBusinessId());
        }

        if (selectionModel.isFilepath()) {
            logLine.setFilepath(generateFilepath());
            logLineJSON.put("filepath", logLine.getFilepath().replace("\\\\", "\\"));
        }

        if (selectionModel.isFileSHA256()) {
            logLine.setFileSHA256(generateFileSHA256());
            logLineJSON.put("fileSHA256", logLine.getFileSHA256());
        }
        if (selectionModel.isDisposition()) {
            logLine.setDisposition(generateDisposition()); // 1 = Clean, 2 = Suspicious, 3 = Malicious, 4 = Unknown
            logLineJSON.put("disposition", logLine.getDisposition());
        }

        // additional code required for malware

        return logLineJSON;
    }

    /**
     * Utility method to generate a timestamp
     * @return a timestamp
     */
    public long generateTimeStamp() {
        return (long) ((System.currentTimeMillis() / 1000) * Math.random());
    }

    /**
     * Utility method to generate processing time
     * @return processing time in seconds
     */
    public long generateProcessingTime() {
        return (long) (Math.random() * 1000);
    }

    /**
     * Utility method to generate a user ID
     * @return a unique id a user
     */
    public UUID generateUserId() {
        return UUID.randomUUID();
    }

    /**
     * Utility method to generate a business ID
     * @return a unique id for a business
     */
    public UUID generateBusinessId() {
        return UUID.randomUUID();
    }

    /**
     * Utility method to generate a filepath
     * @return a random filepath
     */
    public String generateFilepath() {
        // random information
        String[] folders = {"C:/Program Files", "C:/Windows", "C:/Program Files (x86)", "C:/Program Files (x86)/Common Files", "/tmp", "/home"};
        String[] ext = {".pdf", ".xlsx", ".csv", ".txt", ".json", ".sys", ".docx", ".jpg", ".zip"};

        // get random path to file
        Random random = new Random();
        int index = random.nextInt(folders.length);
        String pathToFile = folders[index];

        // get random filename and extension
        index = random.nextInt(ext.length);
        String filename = UUID.randomUUID().toString() + ext[index];

        // combine and return
        File file = new File(pathToFile, filename);
        return file.getAbsolutePath();
    }

    /**
     * Utility method to generate a file SHA256
     * @return a file SHA256
     */
    public UUID generateFileSHA256() {
        return UUID.randomUUID();
    }

    /**
     * Utility method to generate disposition
     * @return a disposition
     */
    public int generateDisposition() {
        return (int) (Math.random() * ((4 - 1) + 1) + 1);
    }

}