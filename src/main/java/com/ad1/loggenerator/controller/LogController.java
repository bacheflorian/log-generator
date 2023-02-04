package com.ad1.loggenerator.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ad1.loggenerator.model.LogMessage;
import com.ad1.loggenerator.model.SelectionModel;
import com.ad1.loggenerator.service.implementation.BatchService;
import com.ad1.loggenerator.service.implementation.StreamingService;

import lombok.AllArgsConstructor;

/**
 * Receives user requests and returns responses via REST API
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/generate")
public class LogController {

    private final BatchService batchService;
    private final StreamingService streamingService;

    // general request for generating batch files or streaming
    @PostMapping("/batch")
    public ResponseEntity<String> generateBatchRequest(@RequestBody SelectionModel selectionModel) {

        if (selectionModel.getMode().equals("Batch")) {
            return new ResponseEntity<>(batchService.batchMode(selectionModel), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid Request. Try again", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/stream")
    public ResponseEntity<String> generateStreamRequest(@RequestBody SelectionModel selectionModel) {
        if (selectionModel.getMode().equals("Stream")) {
            streamingService.setContinueStreaming(true);
            return new ResponseEntity<>(streamingService.streamMode(selectionModel), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid Request. Try again", HttpStatus.BAD_REQUEST);
        }
    }

    // stop streaming request
    @PostMapping("/stream/stop")
    public ResponseEntity<String> stopRequest() {
        streamingService.setContinueStreaming(false);
        return new ResponseEntity<>("Streaming has stopped.", HttpStatus.OK);
    }

    // message destination for batch mode
    @MessageMapping("/batch/{clientId}")
    @SendTo("/topic/batch/{clientId}")
    public LogMessage sendBatchData(LogMessage logMessage, @DestinationVariable int clientId) {
        return logMessage;
    }

}