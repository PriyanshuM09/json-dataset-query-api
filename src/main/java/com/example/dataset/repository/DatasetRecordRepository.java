package com.example.dataset.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dataset.entity.DatasetRecord;

public interface DatasetRecordRepository extends JpaRepository<DatasetRecord, Long> {

    List<DatasetRecord> findByDatasetName(String datasetName);

    boolean existsByDatasetNameAndRecordId(String datasetName, String recordId);
}