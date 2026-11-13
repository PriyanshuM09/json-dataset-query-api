package com.example.dataset.dto;

import java.util.List;
import java.util.Map;

import tools.jackson.databind.JsonNode;

public class GroupedRecordsResponse {

    private Map<String, List<JsonNode>> groupedRecords;

    public GroupedRecordsResponse(Map<String, List<JsonNode>> groupedRecords) {
        this.groupedRecords = groupedRecords;
    }

    public Map<String, List<JsonNode>> getGroupedRecords() {
        return groupedRecords;
    }
}