package com.ad1.loggenerator.service.implementation;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.UUID;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.ad1.loggenerator.exception.FilePathNotFoundException;
import com.ad1.loggenerator.model.SelectionModel;
import com.ad1.loggenerator.model.StreamTracker;

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
    @Async("asyncTaskExecutor")
    public void streamMode(SelectionModel selectionModel, StreamTracker streamJobTracker) {

        // stream to address
        if (!selectionModel.getStreamSettings().getStreamAddress().isEmpty()) {
            streamToAddress(selectionModel, streamJobTracker);
        }
        // stream to file
        else {
            streamToFile(selectionModel, streamJobTracker);
        }
    }

    public String streamToAddress(SelectionModel selectionModel, StreamTracker streamJobTracker) {

        // Setup web client for post request to user specified address
        String streamAddress = selectionModel.getStreamSettings().getStreamAddress();
        WebClient webClient = WebClient.create(streamAddress);
        String[] errorMessage = {""};
        
        while (streamJobTracker.getContinueStreaming()) {
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
                        streamJobTracker.setContinueStreaming(false);
                        errorMessage[0] = "Error while making the request: " + error;
                    }
            );

            // determine if a log lines repeats
            if (Math.random() < selectionModel.getRepeatingLoglinesPercent() && streamJobTracker.getContinueStreaming()) {

                // initiate duplicate post request and receive response from address
                response.subscribe(
                        res -> {
//                            System.out.println("Successful response: " + res);
                        },
                        error -> {
                            streamJobTracker.setContinueStreaming(false);
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

    public void streamToFile(SelectionModel selectionModel, StreamTracker streamJobTracker) {

            
        // create currentTimeDate as a String to append to filepath
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = currentDateTime.format(formatDateTime);

        // reset the start time of the stream
        streamJobTracker.setLastPing(System.currentTimeMillis()/1000);

        // specify filepath location for stream file
        String filename = "C:\\log-generator\\stream\\" + timestamp + ".json";

        try {
            FileWriter fileWriter = new FileWriter(filename);
            while (streamJobTracker.getContinueStreaming()) {
                
                JSONObject logLine = logService.generateLogLine(selectionModel);
                fileWriter.write(logLine.toString() + "\n");

                // determine if a log lines repeats
                if (Math.random() < selectionModel.getRepeatingLoglinesPercent()) {
                    fileWriter.write(logLine.toString() + "\n");
                    streamJobTracker.setLogCount(streamJobTracker.getLogCount() + 1);
                }
                
                fileWriter.write(logLine.toString() + "\n");
                streamJobTracker.setLogCount(streamJobTracker.getLogCount() + 1);
            }
            fileWriter.close();

        } catch (IOException e) {
            throw new FilePathNotFoundException(e.getMessage());
        }
    }

    public String generateJobId() {
        return UUID.randomUUID().toString();
    }
}