package com.ad1.loggenerator.controller;

import com.ad1.loggenerator.model.*;
import com.ad1.loggenerator.service.implementation.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;

/**
 * Receives user requests and returns responses via REST API
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/generate")
@CrossOrigin("http://localhost:3000/")
public class LogsToFileController {

    private final BatchService batchService;
    private final BatchTrackerService batchServiceTracker;
    private final StreamingService streamingService;
    private final StreamTrackerService streamServiceTracker;


    // general request for generating batch files or streaming
    @PostMapping("/batch")
    public ResponseEntity<String> generateBatchRequest(
            @RequestBody SelectionModel selectionModel) throws InterruptedException {
        URL object = null;
        if (selectionModel.getMode().equals("Batch")) {
            String jobId = batchService.generateJobId();
            selectionModel.setJobId(jobId);
            BatchTracker batchJobTracker =
                    new BatchTracker(
                            jobId,
                            0,
                            selectionModel.getBatchSettings().getNumberOfLogs(),
                            System.currentTimeMillis() / 1000,
                            -1,
                            object
                    );
            batchService.batchMode(selectionModel, batchJobTracker);
            batchServiceTracker.addNewJob(batchJobTracker);
            if (batchServiceTracker.getActiveJobsListSize() == 1) {
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
        URL object = null;
        if (selectionModel.getMode().equals("Stream")) {
            String jobId = streamingService.generateJobId();
            selectionModel.setJobId(jobId);
            StreamTracker streamJobTracker = new StreamTracker(
                    jobId,
                    0,
                    System.currentTimeMillis() / 1000,
                    true,
                    System.currentTimeMillis() / 1000,
                    -1,
                    object
            );
            streamingService.streamMode(selectionModel, streamJobTracker);
            streamServiceTracker.addNewJob(streamJobTracker);
            if (streamServiceTracker.getActiveJobsListSize() == 1) {
                streamServiceTracker.checkLastPings();
            }
            return new ResponseEntity<>(jobId, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid Request. Try again", HttpStatus.BAD_REQUEST);
        }
    }
}
