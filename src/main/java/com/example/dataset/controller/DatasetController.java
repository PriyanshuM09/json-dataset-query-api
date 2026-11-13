package com.example.dataset.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dataset.dto.GroupedRecordsResponse;
import com.example.dataset.dto.InsertRecordResponse;
import com.example.dataset.exception.InvalidRequestException;
import com.example.dataset.service.DatasetQueryService;
import com.example.dataset.service.DatasetRecordService;

import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/dataset")
public class DatasetController {

    private final DatasetRecordService recordService;
    private final DatasetQueryService queryService;

    public DatasetController(DatasetRecordService recordService,
                             DatasetQueryService queryService) {
        this.recordService = recordService;
        this.queryService = queryService;
    }

    @PostMapping("/{datasetName}/record")
    public ResponseEntity<InsertRecordResponse> insertRecord(
            @PathVariable String datasetName,
            @RequestBody JsonNode body) {

        InsertRecordResponse response = recordService.insertRecord(datasetName, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{datasetName}/query")
    public ResponseEntity<GroupedRecordsResponse> query(
            @PathVariable String datasetName,
            @RequestParam(required = false) String groupBy) {

        if (groupBy == null || groupBy.isBlank()) {
            throw new InvalidRequestException("Query parameter 'groupBy' is required");
        }

        GroupedRecordsResponse response =
                new GroupedRecordsResponse(queryService.groupBy(datasetName, groupBy));
        return ResponseEntity.ok(response);
    }
}