package com.ad1.loggenerator.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

import java.io.IOException;

public interface AWSLogService {
    public AmazonS3 createS3Client();
    public String createCurrentTimeDate();
    public String generateJobId();
    public int getLogCount(AmazonS3 s3Client, S3Object s3Object, String bucketName, String key) throws IOException;
}
