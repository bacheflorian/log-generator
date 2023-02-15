package com.ad1.loggenerator.service.implementation;

import com.ad1.loggenerator.exception.FilePathNotFoundException;
import com.ad1.loggenerator.model.SelectionModel;
import com.ad1.loggenerator.model.StreamTracker;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Service
public class AWSStreamService {

    @Autowired
    private LogService logService;
    @Autowired
    private AWSLogService awsLogService;

    @Async("asyncTaskExecutor")
    public void streamToS3Buffer(SelectionModel selectionModel, StreamTracker streamJobTracker) throws IOException {
        // specify the s3 bucket and key for the log file
        String bucketName = "stream-s3-log-generator";
        String key = "stream/" + awsLogService.createCurrentTimeDate() + ".json";
        // create s3 client instance
        AmazonS3 s3Client = awsLogService.createS3Client();

        // reset the start time of the stream
        streamJobTracker.setLastPing(System.currentTimeMillis()/1000);

        // specify buffer size for uploading log lines to S3
        int bufferSize = 20 * 1024 * 1024; // 20MB buffer

        // create a temporary buffer to store log lines
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bufferSize);

        // generate and write log lines to buffer
        while (streamJobTracker.getContinueStreaming()) {
            // generate log line
            JSONObject logLine = logService.generateLogLine(selectionModel);

            // write log line to buffer
            buffer.write(logLine.toString().getBytes());
            buffer.write("\n".getBytes());

            // determine if a log line repeats
            if (Math.random() < selectionModel.getRepeatingLoglinesPercent()) {
                buffer.write(logLine.toString().getBytes());
                buffer.write("\n".getBytes());
            }

            // upload buffer to S3 when it is full
            if (buffer.size() >= bufferSize) {
                try (InputStream inputStream = new ByteArrayInputStream(buffer.toByteArray())) {
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(buffer.size());
                    s3Client.putObject(bucketName, key, inputStream, metadata);
                }
                buffer.reset();
            }
        }

        // upload remaining log lines to S3
        if (buffer.size() > 0) {
            try (InputStream inputStream = new ByteArrayInputStream(buffer.toByteArray())) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(buffer.size());
                s3Client.putObject(bucketName, key, inputStream, metadata);
            }
            buffer.reset();
        }
        //Make the s3 object public
        s3Client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
        // Get the url of the s3 object
        URL objectURL = s3Client.getUrl(bucketName, key);
        streamJobTracker.setGetStreamObjectURL(objectURL);
        // Get the s3 object and count the log lines saved to the bucket object
        S3Object s3Object = s3Client.getObject(bucketName, key);
        streamJobTracker.setLogCount(awsLogService.getLogCount(s3Client, s3Object, bucketName, key));
    }

    @Async("asyncTaskExecutor")
    public void streamToS3(SelectionModel selectionModel, StreamTracker streamJobTracker) {
        // specify the s3 bucket and key for the log file
        String bucketName = "stream-s3-log-generator";
        String key = "stream/" + awsLogService.createCurrentTimeDate() + ".json";

        // create s3 client instance
        AmazonS3 s3Client = awsLogService.createS3Client();

        // reset the start time of the stream
        streamJobTracker.setLastPing(System.currentTimeMillis()/1000);

        // create StringBuilder object to append log lines
        StringBuilder stringBuilder = new StringBuilder();

        try {
            while (streamJobTracker.getContinueStreaming()) {
                // generate log line
                JSONObject logLine = logService.generateLogLine(selectionModel);

                // append log line to StringBuilder
                stringBuilder.append(logLine.toString()).append(System.lineSeparator());
                streamJobTracker.setLogCount(streamJobTracker.getLogCount() + 1);

                // determine if a log line repeats
                if (Math.random() < selectionModel.getRepeatingLoglinesPercent()) {
                    // append repeated log line to StringBuilder
                    stringBuilder.append(logLine.toString()).append(System.lineSeparator());
                    streamJobTracker.setLogCount(streamJobTracker.getLogCount() + 1);
                }
            }

            // write StringBuilder content to S3
            byte[] contentAsBytes = stringBuilder.toString().getBytes();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentAsBytes.length);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(contentAsBytes);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, metadata);
            s3Client.putObject(putObjectRequest);

        } catch (SdkClientException e) {
            throw new FilePathNotFoundException(e.getMessage());
        }
        //Make the s3 object public
        s3Client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
        // Get the url of the s3 object
        URL objectURL = s3Client.getUrl(bucketName, key);
        streamJobTracker.setGetStreamObjectURL(objectURL);
    }

}

