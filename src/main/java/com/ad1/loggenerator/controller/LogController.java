package com.ad1.loggenerator.controller;

import com.ad1.loggenerator.model.SelectionModel;
import com.ad1.loggenerator.service.LogService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

/**
 * Receives user requests and returns responses via REST API
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class LogController {

    private final LogService logService;

    // general request for generating batch files or streaming
    @PostMapping("/generate")
    public String generateRequest (@RequestBody SelectionModel selectionModel) {

        if (selectionModel.getModeSelection().equals("Batch")) {
            return logService.batchMode(selectionModel);
        }
        else if (selectionModel.getModeSelection().equals("Stream")) {
            logService.setContinueStreaming(true);
            return logService.streamMode(selectionModel);
        }
        else {
            return "Invalid Request. Try again";
        }
    }

    // stop streaming request
    @PostMapping("/stop")
    public String stopRequest () {
        logService.setContinueStreaming(false);
        return "Streaming has stopped.";
    }

}
