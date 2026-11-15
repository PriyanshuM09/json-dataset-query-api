package com.example.dataset.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dataset.dto.GroupedRecordsResponse;
import com.example.dataset.dto.SortedRecordsResponse;
import com.example.dataset.entity.DatasetRecord;
import com.example.dataset.exception.DatasetNotFoundException;
import com.example.dataset.exception.InvalidRequestException;
import com.example.dataset.operation.GroupByOperation;
import com.example.dataset.operation.SortByOperation;
import com.example.dataset.repository.DatasetRecordRepository;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class DatasetQueryServiceTest {

    @Mock
    private DatasetRecordRepository repository;

    private DatasetQueryService service;

    @BeforeEach
    void setUp() {
        service = new DatasetQueryService(repository, new ObjectMapper(),
                List.of(new GroupByOperation(), new SortByOperation()));
    }

    @Test
    void groupBy_returnsRecordsGroupedByField() {
        when(repository.findByDatasetName("employees")).thenReturn(List.of(
                new DatasetRecord("employees", "1", "{\"id\":1,\"department\":\"Engineering\"}"),
                new DatasetRecord("employees", "2", "{\"id\":2,\"department\":\"Engineering\"}"),
                new DatasetRecord("employees", "3", "{\"id\":3,\"department\":\"Marketing\"}")));

        GroupedRecordsResponse result =
                (GroupedRecordsResponse) service.query("employees", "department", null, null);

        assertEquals(2, result.getGroupedRecords().size());
        assertEquals(2, result.getGroupedRecords().get("Engineering").size());
        assertEquals(1, result.getGroupedRecords().get("Marketing").size());
    }

    @Test
    void sortBy_returnsRecordsSortedByField() {
        when(repository.findByDatasetName("employees")).thenReturn(List.of(
                new DatasetRecord("employees", "1", "{\"id\":1,\"age\":30}"),
                new DatasetRecord("employees", "2", "{\"id\":2,\"age\":25}")));

        SortedRecordsResponse result =
                (SortedRecordsResponse) service.query("employees", null, "age", "asc");

        assertEquals(2, result.getSortedRecords().get(0).get("id").asInt());
        assertEquals(1, result.getSortedRecords().get(1).get("id").asInt());
    }

    @Test
    void query_unknownDataset_throwsNotFound() {
        when(repository.findByDatasetName("ghost")).thenReturn(List.of());

        assertThrows(DatasetNotFoundException.class,
                () -> service.query("ghost", "department", null, null));
    }

    @Test
    void sortBy_invalidOrder_throwsInvalidRequest() {
        when(repository.findByDatasetName("employees")).thenReturn(List.of(
                new DatasetRecord("employees", "1", "{\"id\":1,\"age\":30}")));

        assertThrows(InvalidRequestException.class,
                () -> service.query("employees", null, "age", "sideways"));
    }

    @Test
    void query_bothGroupByAndSortBy_throwsInvalidRequest() {
        assertThrows(InvalidRequestException.class,
                () -> service.query("employees", "department", "age", null));
    }
}