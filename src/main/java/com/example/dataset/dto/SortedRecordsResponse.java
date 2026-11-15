package com.example.dataset.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.databind.JsonNode;

@Schema(description = "Response containing records sorted by a field")
public class SortedRecordsResponse {

    @Schema(description = "List of records sorted by the specified field",
            example = "[{\"id\":1,\"name\":\"John Doe\",\"age\":30},{\"id\":3,\"name\":\"Alice Brown\",\"age\":28},{\"id\":2,\"name\":\"Jane Smith\",\"age\":25}]")
    private List<JsonNode> sortedRecords;

    public SortedRecordsResponse(List<JsonNode> sortedRecords) {
        this.sortedRecords = sortedRecords;
    }

    public List<JsonNode> getSortedRecords() {
        return sortedRecords;
    }
}
