package com.example.dataset.operation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class SortByOperationTest {

    private ObjectMapper mapper = new ObjectMapper();
    private SortByOperation sortBy = new SortByOperation();

    private JsonNode json(String text) throws Exception {
        return mapper.readTree(text);
    }

    @Test
    void sortsAscendingByNumber() throws Exception {
        List<JsonNode> records = List.of(
                json("{\"id\":1,\"age\":30}"),
                json("{\"id\":2,\"age\":25}"),
                json("{\"id\":3,\"age\":28}"));

        List<JsonNode> result = sortBy.apply(records, "age", false);

        assertEquals(2, result.get(0).get("id").asInt());
        assertEquals(3, result.get(1).get("id").asInt());
        assertEquals(1, result.get(2).get("id").asInt());
    }

    @Test
    void sortsDescending() throws Exception {
        List<JsonNode> records = List.of(
                json("{\"id\":1,\"age\":30}"),
                json("{\"id\":2,\"age\":25}"),
                json("{\"id\":3,\"age\":28}"));

        List<JsonNode> result = sortBy.apply(records, "age", true);

        assertEquals(1, result.get(0).get("id").asInt());
        assertEquals(3, result.get(1).get("id").asInt());
        assertEquals(2, result.get(2).get("id").asInt());
    }

    @Test
    void comparesNumbersAsNumbersNotText() throws Exception {
        List<JsonNode> records = List.of(
                json("{\"id\":1,\"age\":100}"),
                json("{\"id\":2,\"age\":9}"),
                json("{\"id\":3,\"age\":10}"));

        List<JsonNode> result = sortBy.apply(records, "age", false);

        assertEquals(9, result.get(0).get("age").asInt());
        assertEquals(10, result.get(1).get("age").asInt());
        assertEquals(100, result.get(2).get("age").asInt());
    }
}