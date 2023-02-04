package com.ad1.loggenerator.service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.ad1.loggenerator.exception.FilePathNotFoundException;
import com.ad1.loggenerator.model.SelectionModel;

import lombok.Data;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

        // stream to address
        if (!selectionModel.getStreamSettings().getStreamAddress().isEmpty()) {
            return streamToAddress(selectionModel);
        }
        // stream to file
        else {
            return streamToFile(selectionModel);
        }
    }

    public String streamToAddress(SelectionModel selectionModel) {

        // Setup web client for post request to user specified address
        String streamAddress = selectionModel.getStreamSettings().getStreamAddress();
        WebClient webClient = WebClient.create(streamAddress);
        String[] errorMessage = {""};

        while (isContinueStreaming()) {
            JSONObject logLine = logService.generateLogLine(selectionModel);

            // set up post request
            Mono<String> response = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(logLine)
                    .retrieve()
                    .bodyToMono(String.class);

            // initiate post request and receive response from address
            response.subscribe(
                    res -> {
//                            System.out.println("Successful response: " + res);
                    },
                    error -> {
                        setContinueStreaming(false);
                        errorMessage[0] = "Error while making the request: " + error;
                    }
            );

            // determine if a log lines repeats
            if (Math.random() < selectionModel.getRepeatingLoglinesPercent() && isContinueStreaming()) {

                // initiate duplicate post request and receive response from address
                response.subscribe(
                        res -> {
//                            System.out.println("Successful response: " + res);
                        },
                        error -> {
                            setContinueStreaming(false);
                            errorMessage[0] = "Error while making the request: " + error;
                        }
                );
            }
        }

        if (!errorMessage[0].isEmpty()) {
            return errorMessage[0];
        }
        return "Successfully streamed to address.";
    }

    public String streamToFile(SelectionModel selectionModel) {

        // create currentTimeDate as a String to append to filepath
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = currentDateTime.format(formatDateTime);

        // specify filepath location for stream file
        String filename = "C:\\log-generator\\stream\\" + timestamp + ".json";

        try {
            FileWriter fileWriter = new FileWriter(filename);

            while (isContinueStreaming()) {
                JSONObject logLine = logService.generateLogLine(selectionModel);
                fileWriter.write(logLine.toString() + "\n");

                // determine if a log lines repeats
                if (Math.random() < selectionModel.getRepeatingLoglinesPercent()) {
                    fileWriter.write(logLine.toString() + "\n");
                }
            }
            fileWriter.close();
            return "Successfully generated stream file";

        } catch (IOException e) {
            throw new FilePathNotFoundException(e.getMessage());
        }
    }

}