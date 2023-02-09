package com.ad1.loggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContinueMessage {
    
    // The stream jobId
    String jobId;
    // Success or failure message
    String message;

}
