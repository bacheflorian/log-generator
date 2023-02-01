package com.ad1.loggenerator.controller;

import com.ad1.loggenerator.model.SelectionModel;
import com.ad1.loggenerator.service.BatchService;
import com.ad1.loggenerator.service.LogService;
import com.ad1.loggenerator.service.StreamingService;
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
    public String generateRequest (@RequestBody SelectionModel selectionModel) {

        if (selectionModel.getModeSelection().equals("Batch")) {
            return batchService.batchMode(selectionModel);
        }
        else if (selectionModel.getModeSelection().equals("Stream")) {
            streamingService.setContinueStreaming(true);
            return streamingService.streamMode(selectionModel);
        }
        else {
            return "Invalid Request. Try again";
        }
    }

    // stop streaming request
    @PostMapping("/stop")
    public String stopRequest () {
        streamingService.setContinueStreaming(false);
        return "Streaming has stopped.";
    }

}
