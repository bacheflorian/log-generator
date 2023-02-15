package com.ad1.loggenerator.service;

import com.ad1.loggenerator.model.BatchTracker;
import com.ad1.loggenerator.model.SelectionModel;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.scheduling.annotation.Async;

import java.net.URL;

public interface AmazonService {
    public AmazonS3 createS3Client();
    public String createCurrentTimeDate();
    void upLoadBatchLogsToS3 (SelectionModel selectionModel, BatchTracker batchJobTracker);
    public String generateJobId();
}
