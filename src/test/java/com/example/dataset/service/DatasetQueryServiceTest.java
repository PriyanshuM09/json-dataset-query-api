package com.example.dataset.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dataset.entity.DatasetRecord;
import com.example.dataset.exception.DatasetNotFoundException;
import com.example.dataset.operation.GroupByOperation;
import com.example.dataset.repository.DatasetRecordRepository;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class DatasetQueryServiceTest {

    @Mock
    private DatasetRecordRepository repository;

    private DatasetQueryService service;

    @BeforeEach
    void setUp() {
        service = new DatasetQueryService(repository, new ObjectMapper(), new GroupByOperation());
    }

    @Test
    void groupBy_returnsRecordsGroupedByField() {
        when(repository.findByDatasetName("employees")).thenReturn(List.of(
                new DatasetRecord("employees", "1", "{\"id\":1,\"department\":\"Engineering\"}"),
                new DatasetRecord("employees", "2", "{\"id\":2,\"department\":\"Engineering\"}"),
                new DatasetRecord("employees", "3", "{\"id\":3,\"department\":\"Marketing\"}")));

        Map<String, List<JsonNode>> result = service.groupBy("employees", "department");

        assertEquals(2, result.size());
        assertEquals(2, result.get("Engineering").size());
        assertEquals(1, result.get("Marketing").size());
    }

    @Test
    void groupBy_unknownDataset_throwsNotFound() {
        when(repository.findByDatasetName("ghost")).thenReturn(List.of());

        assertThrows(DatasetNotFoundException.class,
                () -> service.groupBy("ghost", "department"));
    }
}