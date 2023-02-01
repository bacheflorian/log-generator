package com.ad1.loggenerator.service;

import com.ad1.loggenerator.model.LogModel;
import com.ad1.loggenerator.model.SelectionModel;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import lombok.Data;
import java.util.UUID;

/**
 * Contains logic for processing user requests and generating outputs specific to the user defined parameters
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
