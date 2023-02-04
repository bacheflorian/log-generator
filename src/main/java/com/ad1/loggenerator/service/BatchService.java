package com.ad1.loggenerator.service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.ad1.loggenerator.exception.FilePathNotFoundException;
import com.ad1.loggenerator.model.BatchSettings;
import com.ad1.loggenerator.model.SelectionModel;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Contains logic for processing user requests and generating outputs specific
 * to the user defined parameters
 */
@Data
@AllArgsConstructor
@Service
public class BatchService {

    LogService logService;

    /**
     * Generates and populates the batch file
     * 
     * @param selectionModel defines all the parameters to be included in the batch
     *                       files as per the user
     * @return
     */
    public String batchMode(SelectionModel selectionModel) {
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

                // determine if a log lines repeats
                if (Math.random() < selectionModel.getRepeatingLoglinesPercent()) {
                    fileWriter.write(logLine.toString() + "\n");
                    i++;
                }
            }
            fileWriter.close();

        } catch (IOException e) {
            throw new FilePathNotFoundException(e.getMessage());
        }
        return "Successfully generated batch file";
    }

}