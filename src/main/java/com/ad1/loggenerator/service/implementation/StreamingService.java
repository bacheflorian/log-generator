package com.ad1.loggenerator.service.implementation;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ad1.loggenerator.exception.FilePathNotFoundException;
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

    public StreamingService(@Autowired LogService logService) {
        this.logService = logService;
    }

    /**
     * Generates streaming log lines until it is told to stop
     * 
     * @param selectionModel defines all the parameters to be included in the stream
     *                       log lines as per the user
     * @return
     */
    public String streamMode(SelectionModel selectionModel) {
        try {
            // include logic here to swap between streaming to location vs file as per
            // selectionModel parameters

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
                }
                fileWriter.write(logLine.toString() + "\n");
            }
            fileWriter.close();

        } catch (IOException e) {
            throw new FilePathNotFoundException(e.getMessage());
        }
        return "Successfully generated stream file";
    }

}