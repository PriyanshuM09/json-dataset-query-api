package com.example.dataset.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.dataset.dto.GroupedRecordsResponse;
import com.example.dataset.dto.InsertRecordResponse;
import com.example.dataset.dto.SortedRecordsResponse;
import com.example.dataset.exception.DatasetNotFoundException;
import com.example.dataset.exception.DuplicateRecordException;
import com.example.dataset.exception.InvalidRequestException;
import com.example.dataset.service.DatasetQueryService;
import com.example.dataset.service.DatasetRecordService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(DatasetController.class)
class DatasetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DatasetRecordService service;

    @MockitoBean
    private DatasetQueryService queryService;

    private ObjectMapper mapper = new ObjectMapper();

    private static final String BODY =
            "{\"id\":1,\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}";

    @Test
    void insertRecord_returns201() throws Exception {
        var idNode = mapper.readTree("1");
        when(service.insertRecord(eq("employee_dataset"), any()))
                .thenReturn(new InsertRecordResponse("Record added successfully", "employee_dataset", idNode));

        mockMvc.perform(post("/api/dataset/employee_dataset/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Record added successfully"))
                .andExpect(jsonPath("$.dataset").value("employee_dataset"))
                .andExpect(jsonPath("$.recordId").value(1));
    }

    @Test
    void insertRecord_invalidBody_returns400() throws Exception {
        when(service.insertRecord(any(), any()))
                .thenThrow(new InvalidRequestException("Record must have an 'id' field"));

        mockMvc.perform(post("/api/dataset/employees/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Record must have an 'id' field"));
    }

    @Test
    void insertRecord_duplicate_returns409() throws Exception {
        when(service.insertRecord(any(), any()))
                .thenThrow(new DuplicateRecordException("already exists"));

        mockMvc.perform(post("/api/dataset/employees/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void query_groupBy_returns200() throws Exception {
        var record = mapper.readTree("{\"id\":1,\"name\":\"John\",\"department\":\"Engineering\"}");
        when(queryService.query("employees", "department", null, null))
                .thenReturn(new GroupedRecordsResponse(Map.of("Engineering", List.of(record))));

        mockMvc.perform(get("/api/dataset/employees/query")
                        .param("groupBy", "department"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupedRecords.Engineering[0].name").value("John"));
    }

    @Test
    void query_sortBy_returns200() throws Exception {
        var record = mapper.readTree("{\"id\":2,\"name\":\"Jane\",\"age\":25}");
        when(queryService.query("employees", null, "age", "asc"))
                .thenReturn(new SortedRecordsResponse(List.of(record)));

        mockMvc.perform(get("/api/dataset/employees/query")
                        .param("sortBy", "age")
                        .param("order", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortedRecords[0].name").value("Jane"));
    }

    @Test
    void query_unknownDataset_returns404() throws Exception {
        when(queryService.query("ghost", "department", null, null))
                .thenThrow(new DatasetNotFoundException("Dataset 'ghost' not found"));

        mockMvc.perform(get("/api/dataset/ghost/query")
                        .param("groupBy", "department"))
                .andExpect(status().isNotFound());
    }
    @Test
    void query_unexpectedError_returns500() throws Exception {
        when(queryService.query("employees", "department", null, null))
                .thenThrow(new RuntimeException("database exploded"));

        mockMvc.perform(get("/api/dataset/employees/query")
                        .param("groupBy", "department"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }
}