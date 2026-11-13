package com.example.dataset.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dataset.dto.InsertRecordResponse;
import com.example.dataset.service.DatasetRecordService;

import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/dataset")
public class DatasetController {

    private final DatasetRecordService service;

    public DatasetController(DatasetRecordService service) {
        this.service = service;
    }

    @PostMapping("/{datasetName}/record")
    public ResponseEntity<InsertRecordResponse> insertRecord(
            @PathVariable String datasetName,
            @RequestBody JsonNode body) {

        InsertRecordResponse response = service.insertRecord(datasetName, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}