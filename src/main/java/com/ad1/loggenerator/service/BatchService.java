package com.ad1.loggenerator.service;

import com.ad1.loggenerator.model.SelectionModel;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Contains logic for processing user requests and generating outputs specific to the user defined parameters
 */
@Data
@AllArgsConstructor
@Service
public class BatchService {

    LogService logService;

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
                JSONObject logLine = logService.generateLogLine(selectionModel);
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

}