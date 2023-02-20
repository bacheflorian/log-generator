package com.ad1.loggenerator.service.implementation;

import com.ad1.loggenerator.model.BatchSettings;
import com.ad1.loggenerator.model.BatchTracker;
import com.ad1.loggenerator.model.JobStatus;
import com.ad1.loggenerator.model.SelectionModel;
import com.ad1.loggenerator.service.AmazonService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.S3Object;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AWSBatchService{
    @Autowired
    private LogService logService;
    @Autowired
    private AWSLogService awsLogService;
    /**
     * Generates and populates logs in batch mode
     *
     * @param selectionModel defines all the parameters to be included in the batch
     *                       files as per the user
     */
    @Async("asyncTaskExecutor")
    public void upLoadBatchLogsToS3(SelectionModel selectionModel, BatchTracker batchJobTracker) throws IOException {
        // create s3 client instance and specify s3 bucket name and key
        String bucketName = "batch-s3-log-generator";
        String key = "batch/" + awsLogService.createCurrentTimeDate() + ".json";
        AmazonS3 s3Client = awsLogService.createS3Client();

        try {
            // batch settings
            BatchSettings batchSettings = selectionModel.getBatchSettings();
            // Add log lines to a StringBuilder
            StringBuilder logLines = new StringBuilder();
            for (int i = 0; i < batchSettings.getNumberOfLogs(); i++) {
                JSONObject logLine = logService.generateLogLine(selectionModel);
                logLines.append(logLine.toString() + "\n");
//                batchJobTracker.setLogCount(batchJobTracker.getLogCount() + 1);

                // determine if a log lines repeats
                if (Math.random() < selectionModel.getRepeatingLoglinesPercent()) {
                    logLines.append(logLine.toString() + "\n");
                    i++;
//                    batchJobTracker.setLogCount(batchJobTracker.getLogCount() + 1);
                }
            }
            // Upload the batch file to S3
            s3Client.putObject(bucketName, key, logLines.toString());

        } catch(Exception e){
            // Mark the job as failed if an exception occurred
            batchJobTracker.setStatus(JobStatus.FAILED);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while saving log files to aws S3");
        }
        //Make the s3 object public
        s3Client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
        // Get the url of the s3 object and set it to the BatchTracker
        URL objectURL = s3Client.getUrl(bucketName, key);
        batchJobTracker.setBatchObjectURL(objectURL);
        // Get the s3 object and count the log lines saved to the bucket object
        S3Object s3Object = s3Client.getObject(bucketName, key);
        batchJobTracker.setLogCount(awsLogService.getLogCount(s3Client, s3Object, bucketName, key));
        // Mark the job as completed if no exception occurred
        batchJobTracker.setStatus(JobStatus.COMPLETED);
    }

}
