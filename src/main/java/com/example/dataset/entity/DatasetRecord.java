package com.example.dataset.entity;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "dataset_records",
       uniqueConstraints = @UniqueConstraint(columnNames = {"dataset_name", "record_id"}))
public class DatasetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dataset_name", nullable = false, length = 100)
    private String datasetName;

    @Column(name = "record_id", nullable = false, length = 100)
    private String recordId;

    @Column(name = "payload", nullable = false, columnDefinition = "LONGTEXT")
    private String payload;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected DatasetRecord() { }   // JPA needs an empty constructor

    public DatasetRecord(String datasetName, String recordId, String payload) {
        this.datasetName = datasetName;
        this.recordId = recordId;
        this.payload = payload;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getDatasetName() { return datasetName; }
    public String getRecordId() { return recordId; }
    public String getPayload() { return payload; }
    public Instant getCreatedAt() { return createdAt; }
}