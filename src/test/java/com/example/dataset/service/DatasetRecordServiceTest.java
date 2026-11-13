package com.example.dataset.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dataset.dto.InsertRecordResponse;
import com.example.dataset.entity.DatasetRecord;
import com.example.dataset.exception.DuplicateRecordException;
import com.example.dataset.exception.InvalidRequestException;
import com.example.dataset.repository.DatasetRecordRepository;

import tools.jackson.databind.ObjectMapper;
@ExtendWith(MockitoExtension.class)
class DatasetRecordServiceTest {

    @Mock
    private DatasetRecordRepository repository;

    @InjectMocks
    private DatasetRecordService service;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void insertRecord_savesRecord() throws Exception {
        var body = mapper.readTree("{\"id\":1,\"name\":\"John Doe\"}");
        when(repository.existsByDatasetNameAndRecordId("employees", "1")).thenReturn(false);

        InsertRecordResponse response = service.insertRecord("employees", body);

        assertEquals("Record added successfully", response.getMessage());
        assertEquals("employees", response.getDataset());
        verify(repository).save(any(DatasetRecord.class));
    }

    @Test
    void insertRecord_duplicateId_throwsException() throws Exception {
        var body = mapper.readTree("{\"id\":1}");
        when(repository.existsByDatasetNameAndRecordId("employees", "1")).thenReturn(true);

        assertThrows(DuplicateRecordException.class,
                () -> service.insertRecord("employees", body));
        verify(repository, never()).save(any());
    }

    @Test
    void insertRecord_missingId_throwsException() throws Exception {
        var body = mapper.readTree("{\"name\":\"John\"}");

        assertThrows(InvalidRequestException.class,
                () -> service.insertRecord("employees", body));
    }
}