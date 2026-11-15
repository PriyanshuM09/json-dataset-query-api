package com.example.dataset.operation;

import java.util.List;

import tools.jackson.databind.JsonNode;

public interface QueryOperation {

    String name();

    Object execute(List<JsonNode> records, String field, String order);
}