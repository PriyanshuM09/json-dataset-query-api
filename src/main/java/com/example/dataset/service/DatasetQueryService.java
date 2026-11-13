package com.example.dataset.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.dataset.entity.DatasetRecord;
import com.example.dataset.exception.DatasetNotFoundException;
import com.example.dataset.operation.GroupByOperation;
import com.example.dataset.repository.DatasetRecordRepository;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class DatasetQueryService {

    private final DatasetRecordRepository repository;
    private final ObjectMapper objectMapper;
    private final GroupByOperation groupByOperation;

    public DatasetQueryService(DatasetRecordRepository repository,
                               ObjectMapper objectMapper,
                               GroupByOperation groupByOperation) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.groupByOperation = groupByOperation;
    }

    public Map<String, List<JsonNode>> groupBy(String datasetName, String field) {
        List<JsonNode> records = loadRecords(datasetName);
        return groupByOperation.apply(records, field);
    }

    private List<JsonNode> loadRecords(String datasetName) {
        List<DatasetRecord> rows = repository.findByDatasetName(datasetName);

        if (rows.isEmpty()) {
            throw new DatasetNotFoundException("Dataset '" + datasetName + "' not found");
        }

        List<JsonNode> records = new ArrayList<>();
        for (DatasetRecord row : rows) {
            records.add(objectMapper.readTree(row.getPayload()));
        }
        return records;
    }
}