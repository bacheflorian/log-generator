package com.ad1.loggenerator.service.implementation;

import java.io.File;
import java.util.Random;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.ad1.loggenerator.model.FieldSettings;
import com.ad1.loggenerator.model.LogModel;
import com.ad1.loggenerator.model.SelectionModel;

import lombok.Data;

/**
 * Contains logic for generating outputs specific to the user defined parameters
 */
@Data
@Service
public class LogService {

    /**
     * Generates each log line as defined by the parameters
     * 
     * @param selectionModel defines all the parameters to be included in the log
     *                       lines as per the user
     * @return a single log line in JSON format
     */
    public JSONObject generateLogLine(SelectionModel selectionModel) {

        // generated fields settings
        FieldSettings fieldSettings = selectionModel.getFieldSettings();

        // Create logline objects
        LogModel logLine = new LogModel();
        JSONObject logLineJSON = new JSONObject();

        // Generate log line data
        if (fieldSettings.isIncludeTimeStamp()) {
            logLine.setTimeStamp(generateTimeStamp());
            logLineJSON.put("timeStamp", logLine.getTimeStamp());
        }
        if (fieldSettings.isIncludeProcessingTime()) {
            logLine.setProcessingTime(generateProcessingTime());
            logLineJSON.put("processingTime", logLine.getProcessingTime());
        }
        if (fieldSettings.isIncludeCurrentUserID()) {
            logLine.setUserId(generateUserId());
            logLineJSON.put("userId", logLine.getUserId());
        }
        if (fieldSettings.isIncludeBusinessGUID()) {
            logLine.setBusinessId(generateBusinessId());
            logLineJSON.put("businessId", logLine.getBusinessId());
        }

        if (fieldSettings.isIncludePathToFile()) {
            logLine.setFilepath(generateFilepath());
            logLineJSON.put("filepath", logLine.getFilepath().replace("\\\\", "\\"));
        }

        if (fieldSettings.isIncludeFileSHA256()) {
            logLine.setFileSHA256(generateFileSHA256());
            logLineJSON.put("fileSHA256", logLine.getFileSHA256());
        }
        if (fieldSettings.isIncludeDisposition()) {
            logLine.setDisposition(generateDisposition()); // 1 = Clean, 2 = Suspicious, 3 = Malicious, 4 = Unknown
            logLineJSON.put("disposition", logLine.getDisposition());
        }

        // additional code required for malware

        return logLineJSON;
    }

    /**
     * Utility method to generate a timestamp
     * 
     * @return a timestamp
     */
    public long generateTimeStamp() {
        return (long) ((System.currentTimeMillis() / 1000) * Math.random());
    }

    /**
     * Utility method to generate processing time
     * 
     * @return processing time in seconds
     */
    public long generateProcessingTime() {
        return (long) (Math.random() * 1000);
    }

    /**
     * Utility method to generate a user ID
     * 
     * @return a unique id a user
     */
    public String generateUserId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Utility method to generate a business ID
     * 
     * @return a unique id for a business
     */
    public String generateBusinessId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Utility method to generate a filepath
     * 
     * @return a random filepath
     */
    public String generateFilepath() {
        // random information
        String[] folders = { "C:/Program Files", "C:/Windows", "C:/Program Files (x86)",
                "C:/Program Files (x86)/Common Files", "/tmp", "/home" };
        String[] ext = { ".pdf", ".xlsx", ".csv", ".txt", ".json", ".sys", ".docx", ".jpg", ".zip" };

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
     * 
     * @return a file SHA256
     */
    public String generateFileSHA256() {
        return UUID.randomUUID().toString();
    }

    /**
     * Utility method to generate disposition
     * 
     * @return a disposition
     */
    public int generateDisposition() {
        return (int) (Math.random() * ((4 - 1) + 1) + 1);
    }

}