package com.example.dataset.service;

import org.springframework.stereotype.Service;

import com.example.dataset.dto.InsertRecordResponse;
import com.example.dataset.entity.DatasetRecord;
import com.example.dataset.exception.DuplicateRecordException;
import com.example.dataset.exception.InvalidRequestException;
import com.example.dataset.repository.DatasetRecordRepository;

import tools.jackson.databind.JsonNode;

@Service
public class DatasetRecordService {

    private final DatasetRecordRepository repository;

    public DatasetRecordService(DatasetRecordRepository repository) {
        this.repository = repository;
    }

    public InsertRecordResponse insertRecord(String datasetName, JsonNode body) {
        if (body == null || !body.isObject()) {
            throw new InvalidRequestException("Request body must be a JSON object");
        }

        JsonNode idNode = body.get("id");
        if (idNode == null || idNode.isNull() || idNode.asString().isBlank()) {
            throw new InvalidRequestException("Record must have an 'id' field");
        }

        String recordId = idNode.asString();

        if (repository.existsByDatasetNameAndRecordId(datasetName, recordId)) {
            throw new DuplicateRecordException(
                    "Record with id " + recordId + " already exists in dataset " + datasetName);
        }

        repository.save(new DatasetRecord(datasetName, recordId, body.toString()));

        return new InsertRecordResponse("Record added successfully", datasetName, idNode);
    }
}