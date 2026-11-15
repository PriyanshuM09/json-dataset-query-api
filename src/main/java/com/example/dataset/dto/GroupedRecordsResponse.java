package com.example.dataset.dto;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.databind.JsonNode;

@Schema(description = "Response containing records grouped by a field")
public class GroupedRecordsResponse {

    @Schema(description = "Map of group key to list of records",
            example = "{\"Engineering\":[{\"id\":1,\"name\":\"John Doe\",\"department\":\"Engineering\"}],\"Marketing\":[{\"id\":3,\"name\":\"Alice Brown\",\"department\":\"Marketing\"}]}")
    private Map<String, List<JsonNode>> groupedRecords;

    public GroupedRecordsResponse(Map<String, List<JsonNode>> groupedRecords) {
        this.groupedRecords = groupedRecords;
    }

    public Map<String, List<JsonNode>> getGroupedRecords() {
        return groupedRecords;
    }
}
