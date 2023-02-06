package com.ad1.loggenerator.controller;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ad1.loggenerator.model.BatchTracker;
import com.ad1.loggenerator.model.SelectionModel;
import com.ad1.loggenerator.model.StreamTracker;
import com.ad1.loggenerator.service.implementation.BatchService;
import com.ad1.loggenerator.service.implementation.BatchServiceTracker;
import com.ad1.loggenerator.service.implementation.StreamServiceTracker;
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
    private final BatchServiceTracker batchServiceTracker;
    private final StreamingService streamingService;
    private final StreamServiceTracker streamServiceTracker;

    // general request for generating batch files or streaming
    @PostMapping("/batch")
    public ResponseEntity<String> generateBatchRequest(
        @RequestBody SelectionModel selectionModel) throws InterruptedException {

        if (selectionModel.getMode().equals("Batch")) {
            String jobId = batchService.generateJobId();
            selectionModel.setJobId(jobId);
            BatchTracker batchJobTracker = new BatchTracker(jobId, 0, 
                selectionModel.getBatchSettings().getNumberOfLogs()
                );
            batchService.batchMode(selectionModel, batchJobTracker);
            batchServiceTracker.addNewJob(batchJobTracker);
            if (batchServiceTracker.getJobsListSize() == 1) {
                batchServiceTracker.sendBatchData();
            }
            return new ResponseEntity<>(jobId, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid Request. Try again", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/stream")
    public ResponseEntity<String> generateStreamRequest(
        @RequestBody SelectionModel selectionModel) throws InterruptedException {

        if (selectionModel.getMode().equals("Stream")) {
            String jobId = streamingService.generateJobId();
            selectionModel.setJobId(jobId);
            StreamTracker streamJobTracker = new StreamTracker(jobId, 0, 
                System.currentTimeMillis()/1000, true
                );
            streamingService.streamMode(selectionModel, streamJobTracker);
            streamServiceTracker.addNewJob(streamJobTracker);
            if (streamServiceTracker.getJobsListSize() == 1) {
                streamServiceTracker.checkLastPings();
                streamServiceTracker.sendStreamData();
            }
            return new ResponseEntity<>(jobId, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid Request. Try again", HttpStatus.BAD_REQUEST);
        }
    }

    // stop streaming request
    @PostMapping("/stream/stop/{jobId}")
    public ResponseEntity<String> stopRequest(@PathVariable String jobId) {
        boolean result = streamServiceTracker.stopStreamJob(jobId);

        if (result) {
            return new ResponseEntity<>("Streaming has stopped.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No stream job with that id.", HttpStatus.BAD_REQUEST);
        }
    }

    // continue streaming request
    @PostMapping("/stream/continue/{jobId}")
    public ResponseEntity<String> continueRequest(@PathVariable String jobId) {
        boolean result = streamServiceTracker.continueStreamJob(jobId);

        if (result) {
            return new ResponseEntity<>("Streaming will continue.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No stream job with that id.", HttpStatus.BAD_REQUEST);
        }
    }
    
    // test for streaming to addresss
    @PostMapping("stream/toAddress")
    public ResponseEntity<String> addressStream(@RequestBody JSONObject streamData){
        System.out.println(streamData);
        return new ResponseEntity<>("Data successfully received.", HttpStatus.OK);
    }

}