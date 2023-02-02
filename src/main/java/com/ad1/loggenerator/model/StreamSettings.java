package com.ad1.loggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamSettings {
    // address to stream logs to
    private String streamAddress;

    // if generated logs should be saved
    private boolean saveLogs;
}
