package com.example.dataset.dto;

import tools.jackson.databind.JsonNode;

public class InsertRecordResponse {

    private String message;
    private String dataset;
    private JsonNode recordId;

    public InsertRecordResponse(String message, String dataset, JsonNode recordId) {
        this.message = message;
        this.dataset = dataset;
        this.recordId = recordId;
    }

    public String getMessage() { return message; }
    public String getDataset() { return dataset; }
    public JsonNode getRecordId() { return recordId; }
}