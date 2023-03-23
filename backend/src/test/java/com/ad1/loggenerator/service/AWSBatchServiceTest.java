package com.ad1.loggenerator.service;
import com.ad1.loggenerator.model.*;
import com.ad1.loggenerator.model.fieldsettingsmodels.*;
import com.ad1.loggenerator.service.implementation.AWSBatchService;
import com.ad1.loggenerator.service.implementation.LogService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.S3Object;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Test;


@ExtendWith(MockitoExtension.class)
public class AWSBatchServiceTest {
    @InjectMocks
    private AWSBatchService awsBatchService;

    @DisplayName("Testing upload logs to AWS S3 bucket in batch mode")
    @Test
    public void testUpLoadBatchLogsToS3() throws IOException {
        // given - set up the mock objects and their behavior
        SelectionModel selectionModel = mock(SelectionModel.class);
        BatchTracker batchJobTracker = mock(BatchTracker.class);
        BatchSettings batchSettings = mock(BatchSettings.class);
        FieldSettings fieldSettings = mock(FieldSettings.class);
        TimeStamp timeStamp = mock(TimeStamp.class);
        Disposition disposition = mock(Disposition.class);
        ProcessingTime processingTime = mock(ProcessingTime.class);
        FileSha256 fileSha256 = mock(FileSha256.class);
        BusinessGuid businessGuid = mock(BusinessGuid.class);
        PathToFile pathToFile = mock(PathToFile.class);
        CurrentUserId currentUserId = mock(CurrentUserId.class);

        CustomLog customLog1 = mock(CustomLog.class);
        List<CustomLog> customLogs = new ArrayList<>();
        customLogs.add(customLog1);

        AWSLogService awsLogService = mock(AWSLogService.class);
        LogService logService = mock(LogService.class);
        AmazonS3 s3Client = mock(AmazonS3.class);
        JSONObject logLine = mock(JSONObject.class);
        S3Object s3Object = mock(S3Object.class);

        when(customLog1.getFrequency()).thenReturn(0.2);
        when(selectionModel.getCustomLogs()).thenReturn(customLogs);

        when(batchSettings.getNumberOfLogs()).thenReturn(10);
        when(selectionModel.getBatchSettings()).thenReturn(batchSettings);
        when(selectionModel.getFieldSettings()).thenReturn(fieldSettings);


        when(timeStamp.getInclude()).thenReturn(true);
        when(fieldSettings.getTimeStamp()).thenReturn(timeStamp);

        when(disposition.getInclude()).thenReturn(true);
        when(fieldSettings.getDisposition()).thenReturn(disposition);

        when(processingTime.getInclude()).thenReturn(true);
        when(fieldSettings.getProcessingTime()).thenReturn(processingTime);

        when(fileSha256.getInclude()).thenReturn(true);
        when(fieldSettings.getFileSHA256()).thenReturn(fileSha256);

        when(businessGuid.getInclude()).thenReturn(true);
        when(fieldSettings.getBusinessGUID()).thenReturn(businessGuid);

        when(pathToFile.getInclude()).thenReturn(true);
        when(fieldSettings.getPathToFile()).thenReturn(pathToFile);

        when(currentUserId.getInclude()).thenReturn(true);
        when(fieldSettings.getCurrentUserID()).thenReturn(currentUserId);

        when(logService.generateLogLine(selectionModel)).thenReturn(logLine);


        when(s3Client.getObject(anyString(), anyString())).thenReturn(s3Object);
        when(awsLogService.getLogCount(any(AmazonS3.class), any(S3Object.class), anyString(), anyString())).thenReturn(10);
        when(awsLogService.createS3Client()).thenReturn(s3Client);


        // when - call the method being tested
        awsBatchService = new AWSBatchService(logService, awsLogService);
        awsBatchService.upLoadBatchLogsToS3(selectionModel, batchJobTracker);

        // then - verify that the expected interactions occurred
        verify(logService, times(10)).generateLogLine(selectionModel);
        verify(s3Client, times(1)).putObject(anyString(), anyString(), anyString());
        verify(s3Client, times(1)).setObjectAcl(anyString(), anyString(), eq(CannedAccessControlList.PublicRead));
        verify(s3Client, times(1)).getUrl(anyString(), anyString());
        verify(batchJobTracker, times(1)).setStatus(eq(JobStatus.COMPLETED));
        verify(batchJobTracker, times(1)).setLogCount(eq(10));
    }

}

































