package com.ad1.loggenerator.service;

import com.ad1.loggenerator.model.BatchTracker;
import com.ad1.loggenerator.model.SelectionModel;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.net.URL;

public interface AmazonService {
    public AmazonS3 createS3Client();
    public String createCurrentTimeDate();
    public String generateJobId();
    public int getLogCount(AmazonS3 s3Client, S3Object s3Object, String bucketName, String key) throws IOException;
}
