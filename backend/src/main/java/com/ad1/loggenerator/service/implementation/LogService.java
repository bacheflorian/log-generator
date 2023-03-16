package com.ad1.loggenerator.service.implementation;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.ad1.loggenerator.model.CustomLog;
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
        if (fieldSettings.getTimeStamp().getInclude()) {
            List<Long> values = fieldSettings.getTimeStamp().getValues();
            logLine.setTimeStamp(generateTimeStamp(values));
            logLineJSON.put("timeStamp", logLine.getTimeStamp());
        }
        if (fieldSettings.getProcessingTime().getInclude()) {
            List<Long> values = fieldSettings.getProcessingTime().getValues();
            logLine.setProcessingTime(generateProcessingTime(values));
            logLineJSON.put("processingTime", logLine.getProcessingTime());
        }
        if (fieldSettings.getCurrentUserID().getInclude()) {
            List<String> values = fieldSettings.getCurrentUserID().getValues();
            logLine.setUserId(generateUserId(values));
            logLineJSON.put("userId", logLine.getUserId());
        }
        if (fieldSettings.getBusinessGUID().getInclude()) {
            List<String> values = fieldSettings.getBusinessGUID().getValues();
            logLine.setBusinessId(generateBusinessId(values));
            logLineJSON.put("businessId", logLine.getBusinessId());
        }
        if (fieldSettings.getPathToFile().getInclude()) {
            List<String> values = fieldSettings.getPathToFile().getValues();
            logLine.setFilepath(generateFilepath(values));
            logLineJSON.put("filepath", logLine.getFilepath().replace("\\\\", "\\"));
        }
        if (fieldSettings.getFileSHA256().getInclude()) {
            List<String> values = fieldSettings.getFileSHA256().getValues();
            logLine.setFileSHA256(generateFileSHA256(values));
            logLineJSON.put("fileSHA256", logLine.getFileSHA256());
        }
        if (fieldSettings.getDisposition().getInclude()) {
            List<Integer> values = fieldSettings.getDisposition().getValues();
            logLine.setDisposition(generateDisposition(values)); // 1 = Clean, 2 = Suspicious, 3 = Malicious, 4 = Unknown
            logLineJSON.put("disposition", logLine.getDisposition());
        }

        List<CustomLog> customLogs = selectionModel.getCustomLogs();

        // choose a random custom log based on frequency
        CustomLog customLog = chooseCustomLog(customLogs);

        if (customLogs.size() == 0) {
            return logLineJSON;
        }

        if (customLog == null) {
            // get one of the custom logs to add their fields to the log line
            // that don't exist on the log line as null
            addNullCustomLogFields(logLineJSON, customLogs.get(0));
        } else {
            // add the fields from the custom log to the log line, overwriting
            // any of the randomly generated fields
            addCustomLogFields(logLineJSON, customLog);
        }

        // additional code required for malware

        return logLineJSON;
    }

    /**
     * Removes the fields that should not be included in the custom logs
     * 
     * @param customLogs
     * @param selectionModel
     */
    public void removeExcludedFields(List<CustomLog> customLogs, SelectionModel selectionModel) {

        FieldSettings fieldSettings = selectionModel.getFieldSettings();

        for (CustomLog customLog: customLogs) {
            if (customLog.getFields().containsKey("timeStamp")
                        && !fieldSettings.getTimeStamp().getInclude()) {
                customLog.getFields().remove("timeStamp");
            }
            if (customLog.getFields().containsKey("processingTime")
                        && !fieldSettings.getProcessingTime().getInclude()) {
                customLog.getFields().remove("processingTime");
            }
            if (customLog.getFields().containsKey("userId")
                        && !fieldSettings.getCurrentUserID().getInclude()) {
                customLog.getFields().remove("userId");
            }
            if (customLog.getFields().containsKey("businessId")
                        && !fieldSettings.getBusinessGUID().getInclude()) {
                customLog.getFields().remove("businessId");
            }
            if (customLog.getFields().containsKey("filepath")
                        && !fieldSettings.getPathToFile().getInclude()) {
                customLog.getFields().remove("filepath");
            }
            if (customLog.getFields().containsKey("fileSHA256")
                        && !fieldSettings.getFileSHA256().getInclude()) {
                customLog.getFields().remove("fileSHA256");
            }
            if (customLog.getFields().containsKey("disposition")
                        && !fieldSettings.getDisposition().getInclude()) {
                customLog.getFields().remove("disposition");
            }
        }
    }

    /**
     * This method takes the fields in the custom log and adds them to the log
     * line generated. If the custom log has a field that is part of our model,
     * it will overwrite the randomly generated value
     * 
     * @param logLineJSON
     * @param customLog
     */
    private void addCustomLogFields(JSONObject logLineJSON, CustomLog customLog) {

        for (String key: customLog.getFields().keySet()) {
            logLineJSON.put(key, customLog.getFields().get(key));
        }

    }

    /**
     * This method will take the fields in the custom log and add fields that
     * do not currently exist in the generated log with a value of null
     * 
     * @param logLineJSON
     * @param customLog
     */
    private void addNullCustomLogFields(JSONObject logLineJSON, CustomLog customLog) {
        
        for (String key: customLog.getFields().keySet()) {

            if (!logLineJSON.containsKey(key)) {
                // If the generated log does not have the field yet, add it
                logLineJSON.put(key, null);
            }
        }

    }

    /**
     * Randomly choose a custom log to generate. The frequencies in the List 
     * must total <= 1.00. This method also returns null when a custom log
     * is not selected and a random log should be generated
     * 
     * @param customLogs
     * @return
     */
    private CustomLog chooseCustomLog(List<CustomLog> customLogs) {
        
        // get a random value
        Random random = new Random();
        double randomDouble = random.nextDouble(1);

        double lower = 0;
        double upper = 0;
        for (CustomLog customLog: customLogs) {
            upper += customLog.getFrequency();
            
            // return the custom log chosen
            if (randomDouble <= upper && randomDouble > lower) {
                return customLog;
            }
            lower = upper;
        }

        // return null if no custom log chosen
        return null;
    }

    /**
     * Utility method to generate a timestamp
     * 
     * @return a timestamp
     */
    public long generateTimeStamp(List<Long> values) {
        
        if (values.size() == 0) {
            // if no value provided, generate random timestamp
            return generateRandomTimeStamp();
        }

        // if value provided, select one at random
        Random random = new Random();
        int randomIndex = random.nextInt(values.size());
        return values.get(randomIndex);
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
    public long generateProcessingTime(List<Long> values) {

        if (values.size() == 0) {
            // if no value provided, generate random processing time
            return generateRandomProcessingTime();
        }

        // if value provided, select one at random
        Random random = new Random();
        int randomIndex = random.nextInt(values.size());
        return values.get(randomIndex);
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
    public String generateUserId(List<String> values) {

        if (values.size() == 0) {
            // if no value provided, generate random user id
            return generateRandomUserId();
        }

        // if value provided, select one at random
        Random random = new Random();
        int randomIndex = random.nextInt(values.size());
        return values.get(randomIndex);
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
    public String generateBusinessId(List<String> values) {

        if (values.size() == 0) {
            // if no value provided, generate random business id
            return generateRandomBusinessId();
        }

        // if value provided, select one at random
        Random random = new Random();
        int randomIndex = random.nextInt(values.size());
        return values.get(randomIndex);
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
    public String generateFilepath(List<String> values) {

        if (values.size() == 0) {
            // if no value provided, generate random file path
            return generateRandomFilepath();
        }

        // if value provided, select one at random
        Random random = new Random();
        int randomIndex = random.nextInt(values.size());
        return values.get(randomIndex);
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
    public String generateFileSHA256(List<String> values) {

        if (values.size() == 0) {
            // if no value provided, generate random file sha256
            return generateRandomFileSHA256();
        }

        // if value provided, select one at random
        Random random = new Random();
        int randomIndex = random.nextInt(values.size());
        return values.get(randomIndex);
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
    public int generateDisposition(List<Integer> values) {

        if (values.size() == 0) {
            // if no value provided, generate random disposition
            return generateRandomDisposition();
        }

        // if value provided, select one at random
        Random random = new Random();
        int randomIndex = random.nextInt(values.size());
        return values.get(randomIndex);
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