package com.ad1.loggenerator.controller;

import com.ad1.loggenerator.model.SelectionModel;
import com.ad1.loggenerator.service.BatchService;
import com.ad1.loggenerator.service.LogService;
<<<<<<< HEAD

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
=======
import com.ad1.loggenerator.service.StreamingService;
>>>>>>> upstream/main
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
<<<<<<< HEAD
            return new ResponseEntity<>(logService.batchMode(selectionModel), HttpStatus.OK);
        }
        else if (selectionModel.getModeSelection().equals("Stream")) {
            logService.setContinueStreaming(true);
            return new ResponseEntity<>(logService.streamMode(selectionModel), HttpStatus.OK);
=======
            return batchService.batchMode(selectionModel);
        }
        else if (selectionModel.getModeSelection().equals("Stream")) {
            streamingService.setContinueStreaming(true);
            return streamingService.streamMode(selectionModel);
>>>>>>> upstream/main
        }
        else {
            return new ResponseEntity<>("Invalid Request. Try again", HttpStatus.BAD_REQUEST);
        }
    }

    // stop streaming request
    @PostMapping("/stop")
<<<<<<< HEAD
    public ResponseEntity<String> stopRequest () {
        logService.setContinueStreaming(false);
        return new ResponseEntity<>("Streaming has stopped.", HttpStatus.OK);
=======
    public String stopRequest () {
        streamingService.setContinueStreaming(false);
        return "Streaming has stopped.";
>>>>>>> upstream/main
    }

}
