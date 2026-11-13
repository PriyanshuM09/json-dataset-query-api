package com.example.dataset.operation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class GroupByOperationTest {

    private ObjectMapper mapper = new ObjectMapper();
    private GroupByOperation groupBy = new GroupByOperation();

    private JsonNode json(String text) throws Exception {
        return mapper.readTree(text);
    }

    @Test
    void groupsRecordsByField() throws Exception {
        List<JsonNode> records = List.of(
                json("{\"id\":1,\"name\":\"John\",\"department\":\"Engineering\"}"),
                json("{\"id\":2,\"name\":\"Jane\",\"department\":\"Engineering\"}"),
                json("{\"id\":3,\"name\":\"Alice\",\"department\":\"Marketing\"}"));

        Map<String, List<JsonNode>> result = groupBy.apply(records, "department");

        assertEquals(2, result.size());
        assertEquals(2, result.get("Engineering").size());
        assertEquals(1, result.get("Marketing").size());
    }

    @Test
    void keepsOriginalOrderInsideEachGroup() throws Exception {
        List<JsonNode> records = List.of(
                json("{\"id\":1,\"department\":\"Engineering\"}"),
                json("{\"id\":2,\"department\":\"Engineering\"}"));

        Map<String, List<JsonNode>> result = groupBy.apply(records, "department");

        assertEquals(1, result.get("Engineering").get(0).get("id").asInt());
        assertEquals(2, result.get("Engineering").get(1).get("id").asInt());
    }

    @Test
    void putsRecordsWithoutTheFieldUnderNullGroup() throws Exception {
        List<JsonNode> records = List.of(
                json("{\"id\":1,\"department\":\"Engineering\"}"),
                json("{\"id\":2,\"name\":\"No department\"}"));

        Map<String, List<JsonNode>> result = groupBy.apply(records, "department");

        assertEquals(1, result.get("null").size());
    }
}