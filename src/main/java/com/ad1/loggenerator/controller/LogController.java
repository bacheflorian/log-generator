package com.ad1.loggenerator.controller;

import com.ad1.loggenerator.model.SelectionModel;
import com.ad1.loggenerator.service.BatchService;
import com.ad1.loggenerator.service.LogService;
import com.ad1.loggenerator.service.StreamingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

/**
 * Receives user requests and returns responses via REST API
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class LogController {

    private final BatchService batchService;
    private final StreamingService streamingService;

    // general request for generating batch files or streaming
    @PostMapping("/generate")
    public ResponseEntity<String> generateRequest (@RequestBody SelectionModel selectionModel) {

        if (selectionModel.getModeSelection().equals("Batch")) {
            return new ResponseEntity<>(batchService.batchMode(selectionModel), HttpStatus.OK);
        }
        else if (selectionModel.getModeSelection().equals("Stream")) {
            streamingService.setContinueStreaming(true);
            return new ResponseEntity<>(streamingService.streamMode(selectionModel), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Invalid Request. Try again", HttpStatus.BAD_REQUEST);
        }
    }

    // stop streaming request
    @PostMapping("/stop")
    public ResponseEntity<String> stopRequest () {
        streamingService.setContinueStreaming(false);
        return new ResponseEntity<>("Streaming has stopped.", HttpStatus.OK);
    }
}