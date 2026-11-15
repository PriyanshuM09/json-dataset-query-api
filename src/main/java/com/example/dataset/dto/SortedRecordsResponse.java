package com.example.dataset.dto;

import java.util.List;

import tools.jackson.databind.JsonNode;

public class SortedRecordsResponse {

    private List<JsonNode> sortedRecords;

    public SortedRecordsResponse(List<JsonNode> sortedRecords) {
        this.sortedRecords = sortedRecords;
    }

    public List<JsonNode> getSortedRecords() {
        return sortedRecords;
    }
}