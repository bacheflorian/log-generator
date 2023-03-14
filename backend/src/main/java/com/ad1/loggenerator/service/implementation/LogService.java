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
        if (fieldSettings.getTimeStamp().isInclude()) {
            long values[] = fieldSettings.getTimeStamp().getValues();
            logLine.setTimeStamp(generateTimeStamp(values));
            logLineJSON.put("timeStamp", logLine.getTimeStamp());
        }
        if (fieldSettings.getProcessingTime().isInclude()) {
            long values[] = fieldSettings.getProcessingTime().getValues();
            logLine.setProcessingTime(generateProcessingTime(values));
            logLineJSON.put("processingTime", logLine.getProcessingTime());
        }
        if (fieldSettings.getCurrentUserID().isInclude()) {
            String values[] = fieldSettings.getCurrentUserID().getValues();
            logLine.setUserId(generateUserId(values));
            logLineJSON.put("userId", logLine.getUserId());
        }
        if (fieldSettings.getBusinessGUID().isInclude()) {
            String values[] = fieldSettings.getBusinessGUID().getValues();
            logLine.setBusinessId(generateBusinessId(values));
            logLineJSON.put("businessId", logLine.getBusinessId());
        }

        if (fieldSettings.getPathToFile().isInclude()) {
            String values[] = fieldSettings.getPathToFile().getValues();
            logLine.setFilepath(generateFilepath(values));
            logLineJSON.put("filepath", logLine.getFilepath().replace("\\\\", "\\"));
        }

        if (fieldSettings.getFileSHA256().isInclude()) {
            String values[] = fieldSettings.getFileSHA256().getValues();
            logLine.setFileSHA256(generateFileSHA256(values));
            logLineJSON.put("fileSHA256", logLine.getFileSHA256());
        }
        if (fieldSettings.getDisposition().isInclude()) {
            int values[] = fieldSettings.getDisposition().getValues();
            logLine.setDisposition(generateDisposition(values)); // 1 = Clean, 2 = Suspicious, 3 = Malicious, 4 = Unknown
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
    public long generateTimeStamp(long[] values) {
        
        if (values.length == 0) {
            // if no value provided, generate random timestamp
            return generateRandomTimeStamp();
        }

        // if value provided, select one at random
        Random random = new Random();
        int randomIndex = random.nextInt(values.length);
        return values[randomIndex];
    }

    /**
     * Utility method to generate a random timestamp
     * 
     * @return a timestamp
     */
    public long generateRandomTimeStamp() {
        return (long) ((System.currentTimeMillis() / 1000) * Math.random());
    }

    /**
     * Utility method to generate processing time
     * 
     * @return processing time in seconds
     */
    public long generateProcessingTime(long[] values) {

        if (values.length == 0) {
            // if no value provided, generate random processing time
            return generateRandomProcessingTime();
        }

        // if value provided, select one at random
        Random random = new Random();
        int randomIndex = random.nextInt(values.length);
        return values[randomIndex];
    }

    /**
     * Utility method to generate a random processing time
     * 
     * @return random processing time in seconds
     */
    public long generateRandomProcessingTime() {
        return (long) (Math.random() * 1000);
    }

    /**
     * Utility method to generate a user ID
     * 
     * @return a unique id a user
     */
    public String generateUserId(String[] values) {

        if (values.length == 0) {
            // if no value provided, generate random user id
            return generateRandomUserId();
        }

        // if value provided, select one at random
        Random random = new Random();
        int randomIndex = random.nextInt(values.length);
        return values[randomIndex];
    }

    /**
     * Utility method to generate a random user ID
     * 
     * @return a random unique id a user
     */
    public String generateRandomUserId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Utility method to generate a business ID
     * 
     * @return a unique id for a business
     */
    public String generateBusinessId(String[] values) {

        if (values.length == 0) {
            // if no value provided, generate random business id
            return generateRandomBusinessId();
        }

        // if value provided, select one at random
        Random random = new Random();
        int randomIndex = random.nextInt(values.length);
        return values[randomIndex];
    }

    /**
     * Utility method to generate a random business ID
     * 
     * @return a random unique id for a business
     */
    public String generateRandomBusinessId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Utility method to generate a filepath
     * 
     * @return a random filepath
     */
    public String generateFilepath(String[] values) {

        if (values.length == 0) {
            // if no value provided, generate random file path
            return generateRandomFilepath();
        }

        // if value provided, select one at random
        Random random = new Random();
        int randomIndex = random.nextInt(values.length);
        return values[randomIndex];
    }

    /**
     * Utility method to generate a random filepath
     * 
     * @return a random filepath
     */
    public String generateRandomFilepath() {
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
    public String generateFileSHA256(String[] values) {

        if (values.length == 0) {
            // if no value provided, generate random file sha256
            return generateRandomFileSHA256();
        }

        // if value provided, select one at random
        Random random = new Random();
        int randomIndex = random.nextInt(values.length);
        return values[randomIndex];
    }

    /**
     * Utility method to generate a random file SHA256
     * 
     * @return a random file SHA256
     */
    public String generateRandomFileSHA256() {
        return UUID.randomUUID().toString();
    }

    /**
     * Utility method to generate disposition
     * 
     * @return a disposition
     */
    public int generateDisposition(int[] values) {

        if (values.length == 0) {
            // if no value provided, generate random disposition
            return generateRandomDisposition();
        }

        // if value provided, select one at random
        Random random = new Random();
        int randomIndex = random.nextInt(values.length);
        return values[randomIndex];
    }

    /**
     * Utility method to generate a random disposition
     * 
     * @return a random disposition
     */
    public int generateRandomDisposition() {
        return (int) (Math.random() * ((4 - 1) + 1) + 1);
    }

}