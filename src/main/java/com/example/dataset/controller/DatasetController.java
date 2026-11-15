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

import com.example.dataset.dto.ErrorResponse;
import com.example.dataset.dto.InsertRecordResponse;
import com.example.dataset.service.DatasetQueryService;
import com.example.dataset.service.DatasetRecordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import tools.jackson.databind.JsonNode;

@Tag(name = "Dataset", description = "Insert and query JSON records")
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

    @Operation(
        summary = "Insert a JSON record into a dataset",
        description = "The record must contain an 'id'. A duplicate id in the same dataset returns 409.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "object"),
                examples = @ExampleObject(
                    value = "{\"id\": 1, \"name\": \"John Doe\", \"age\": 30, \"department\": \"Engineering\"}"))),
        responses = {
            @ApiResponse(responseCode = "201", description = "Record created",
                content = @Content(schema = @Schema(implementation = InsertRecordResponse.class))),
            @ApiResponse(responseCode = "400", description = "Missing 'id' field or non-object body",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate record id in this dataset",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    @PostMapping("/{datasetName}/record")
    public ResponseEntity<InsertRecordResponse> insertRecord(
            @Parameter(description = "Dataset name", example = "employee_dataset")
            @PathVariable String datasetName,
            @RequestBody JsonNode body) {

        InsertRecordResponse response = recordService.insertRecord(datasetName, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Query a dataset with group-by or sort-by",
        description = "Pass either 'groupBy' or 'sortBy', not both. 'order' is asc (default) or desc.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Query result",
                content = @Content(mediaType = "application/json",
                    examples = {
                        @ExampleObject(name = "groupBy",
                            summary = "Group by response",
                            value = "{\"groupedRecords\":{\"Engineering\":[{\"id\":1,\"name\":\"John Doe\",\"department\":\"Engineering\"}],\"Marketing\":[{\"id\":3,\"name\":\"Alice Brown\",\"department\":\"Marketing\"}]}}"),
                        @ExampleObject(name = "sortBy",
                            summary = "Sort by response",
                            value = "{\"sortedRecords\":[{\"id\":1,\"name\":\"John Doe\",\"age\":30},{\"id\":3,\"name\":\"Alice Brown\",\"age\":28},{\"id\":2,\"name\":\"Jane Smith\",\"age\":25}]}")
                    })),
            @ApiResponse(responseCode = "400", description = "Invalid query (both groupBy and sortBy, or invalid order)",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Dataset not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    @GetMapping("/{datasetName}/query")
    public ResponseEntity<Object> query(
            @Parameter(description = "Dataset name", example = "employee_dataset")
            @PathVariable String datasetName,
            @Parameter(description = "Field to group by", example = "department")
            @RequestParam(required = false) String groupBy,
            @Parameter(description = "Field to sort by", example = "age")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "asc or desc (default asc)", example = "asc")
            @RequestParam(required = false) String order) {

        return ResponseEntity.ok(queryService.query(datasetName, groupBy, sortBy, order));
    }
}
