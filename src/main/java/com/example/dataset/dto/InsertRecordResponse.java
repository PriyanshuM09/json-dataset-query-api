package com.example.dataset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.databind.JsonNode;

public class InsertRecordResponse {

    private String message;
    private String dataset;

    @Schema(implementation = Object.class, example = "1")
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
