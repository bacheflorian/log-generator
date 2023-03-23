package com.ad1.loggenerator.controller;

import com.ad1.loggenerator.exception.AddressNotFoundException;
import com.ad1.loggenerator.model.*;
import com.ad1.loggenerator.service.implementation.*;

import jakarta.validation.Valid;

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
@CrossOrigin("http://ec2-52-38-219-170.us-west-2.compute.amazonaws.com/")
public class LogsToFileController {

    private final BatchService batchService;
    private final BatchTrackerService batchServiceTracker;
    private final StreamingService streamingService;
    private final StreamTrackerService streamServiceTracker;


    // general request for generating batch files or streaming
    @PostMapping("/batch")
    public ResponseEntity<String> generateBatchRequest(
            @Valid @RequestBody SelectionModel selectionModel) throws InterruptedException {

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
                            object,
                            JobStatus.ACTIVE
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
            @Valid @RequestBody SelectionModel selectionModel) throws InterruptedException {
        URL object = null;
        if (selectionModel.getMode().equals("Stream")) {

            // If a stream address was specified, check the address
            if (!selectionModel.getStreamSettings().getStreamAddress().isEmpty()) {
                boolean isAddressAvailable = streamingService.isAddressAvailable(selectionModel);
                if (!isAddressAvailable) {
                    throw new AddressNotFoundException("Stream address " +
                        selectionModel.getStreamSettings().getStreamAddress() +
                        " is not available."
                    );
                    
                }
            }

            String jobId = streamingService.generateJobId();
            selectionModel.setJobId(jobId);
            StreamTracker streamJobTracker = new StreamTracker(
                    jobId,
                    0,
                    System.currentTimeMillis() / 1000,
                    JobStatus.ACTIVE,
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
