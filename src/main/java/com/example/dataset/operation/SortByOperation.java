package com.example.dataset.operation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;

@Component
public class SortByOperation {

    public List<JsonNode> apply(List<JsonNode> records, String field, boolean descending) {
        List<JsonNode> sorted = new ArrayList<>(records);
        sorted.sort((a, b) -> compare(a.get(field), b.get(field), descending));
        return sorted;
    }

    private int compare(JsonNode first, JsonNode second, boolean descending) {
        boolean firstMissing = isMissing(first);
        boolean secondMissing = isMissing(second);

        if (firstMissing && secondMissing) {
            return 0;
        }
        if (firstMissing) {
            return 1;
        }
        if (secondMissing) {
            return -1;
        }

        int result;
        if (first.isNumber() && second.isNumber()) {
            result = first.decimalValue().compareTo(second.decimalValue());
        } else {
            result = textOf(first).compareTo(textOf(second));
        }

        if (descending) {
            return -result;
        }
        return result;
    }

    private boolean isMissing(JsonNode value) {
        return value == null || value.isNull();
    }

    private String textOf(JsonNode value) {
        if (value.isValueNode()) {
            return value.asString();
        }
        return value.toString();
    }
}