package com.example.dataset.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.dataset.entity.DatasetRecord;
import com.example.dataset.exception.DatasetNotFoundException;
import com.example.dataset.exception.InvalidRequestException;
import com.example.dataset.operation.QueryOperation;
import com.example.dataset.repository.DatasetRecordRepository;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class DatasetQueryService {

    private final DatasetRecordRepository repository;
    private final ObjectMapper objectMapper;
    private final Map<String, QueryOperation> operations = new HashMap<>();

    public DatasetQueryService(DatasetRecordRepository repository,
                               ObjectMapper objectMapper,
                               List<QueryOperation> operationList) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        for (QueryOperation operation : operationList) {
            operations.put(operation.name(), operation);
        }
    }

    public Object query(String datasetName, String groupBy, String sortBy, String order) {
        boolean hasGroupBy = groupBy != null && !groupBy.isBlank();
        boolean hasSortBy = sortBy != null && !sortBy.isBlank();

        if (hasGroupBy && hasSortBy) {
            throw new InvalidRequestException("Use either 'groupBy' or 'sortBy', not both");
        }

        String operationName;
        String field;
        if (hasGroupBy) {
            operationName = "groupBy";
            field = groupBy;
        } else if (hasSortBy) {
            operationName = "sortBy";
            field = sortBy;
        } else {
            throw new InvalidRequestException("Provide either 'groupBy' or 'sortBy' query parameter");
        }

        List<JsonNode> records = loadRecords(datasetName);
        return operations.get(operationName).execute(records, field, order);
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