package com.ad1.loggenerator.service;

import com.ad1.loggenerator.model.SelectionModel;
import org.json.simple.JSONObject;

import java.util.UUID;

public interface LogServiceInterface {
    public JSONObject generateLogLine(SelectionModel selectionModel);
    public long generateTimeStamp();
    public long generateProcessingTime();
    public UUID generateUserId();
    public UUID generateBusinessId();
    public String generateFilepath();
    public UUID generateFileSHA256();
    public int generateDisposition();
}
