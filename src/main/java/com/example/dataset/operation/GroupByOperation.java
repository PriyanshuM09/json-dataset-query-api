package com.example.dataset.operation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;

@Component
public class GroupByOperation {

    public Map<String, List<JsonNode>> apply(List<JsonNode> records, String field) {
        Map<String, List<JsonNode>> groups = new LinkedHashMap<>();

        for (JsonNode record : records) {
            String key = getKey(record, field);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
        }

        return groups;
    }

    private String getKey(JsonNode record, String field) {
        JsonNode value = record.get(field);
        if (value == null || value.isNull()) {
            return "null";
        }
        if (value.isValueNode()) {
            return value.asString();
        }
        return value.toString();
    }
}