# JSON Dataset Query API

A Spring Boot REST API that stores arbitrary JSON records in named datasets and supports **group-by** and **sort-by** query operations. Built as a backend assignment demonstrating JSON storage, dynamic grouping/sorting in Java, and clean REST design.

## Live URLs

| Resource | URL |
|----------|-----|
| API Base | https://json-dataset-query-api-production.up.railway.app |
| Swagger UI | https://json-dataset-query-api-production.up.railway.app/swagger-ui/index.html |

## Tech Stack

- Java 17, Spring Boot 4.1.2
- Spring Data JPA + MySQL 8
- Jackson 3 (`tools.jackson.databind`)
- springdoc-openapi 3.2.0 (Swagger UI)
- JUnit 5 + Mockito + MockMvc
- Docker (multi-stage build), deployed on Railway

## API Endpoints

### POST `/api/dataset/{datasetName}/record`

Insert a JSON record. The body must be a JSON object containing an `"id"` field.

**Request:**
```json
{
  "id": 1,
  "name": "John Doe",
  "age": 30,
  "department": "Engineering"
}
```

**Response (201 Created):**
```json
{
  "message": "Record added successfully",
  "dataset": "employee_dataset",
  "recordId": 1
}
```

### GET `/api/dataset/{datasetName}/query?groupBy={field}`

Group all records in a dataset by the given field.

**Response (200 OK):**
```json
{
  "groupedRecords": {
    "Engineering": [
      { "id": 1, "name": "John Doe", "age": 30, "department": "Engineering" },
      { "id": 2, "name": "Jane Smith", "age": 25, "department": "Engineering" }
    ],
    "Marketing": [
      { "id": 3, "name": "Alice Brown", "age": 28, "department": "Marketing" }
    ]
  }
}
```

### GET `/api/dataset/{datasetName}/query?sortBy={field}&order=asc|desc`

Sort all records in a dataset by the given field. `order` defaults to `asc`.

**Response (200 OK):**
```json
{
  "sortedRecords": [
    { "id": 1, "name": "John Doe", "age": 30, "department": "Engineering" },
    { "id": 3, "name": "Alice Brown", "age": 28, "department": "Marketing" },
    { "id": 2, "name": "Jane Smith", "age": 25, "department": "Engineering" }
  ]
}
```

### Error Codes

| Status | Meaning |
|--------|---------|
| 400 | Missing `id` field, non-object body, malformed JSON, invalid `order` value, or both `groupBy` and `sortBy` provided |
| 404 | Dataset not found |
| 409 | Duplicate record id within the same dataset |
| 500 | Unexpected server error |

**Error response format:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Record must have an 'id' field"
}
```

## Design Decisions & Assumptions

- **JSON stored as LONGTEXT payload** — grouping and sorting happen in Java so any dataset shape works without schema changes.
- **Unique constraint on `(dataset_name, record_id)`** gives a 409 on duplicates, backed by a service-level check before insert.
- **Strategy pattern** via `QueryOperation` interface — `GroupByOperation` and `SortByOperation` are injected as a list into `DatasetQueryService`, which selects the right strategy by name.
- Records **missing the group-by field** go under a `"null"` group.
- **Missing or null sort values sort last** (after all non-null values).
- **Numbers compare numerically**; everything else compares as text.
- Using both `groupBy` and `sortBy` together returns **400 Bad Request**.
- `order` defaults to **asc** if omitted.

## How to Run Locally

### Requirements
- JDK 17
- MySQL 8

### Steps

1. Create the database:
   ```sql
   CREATE DATABASE dataset_db;
   ```

2. Create `src/main/resources/application-local.properties` (this file is gitignored):
   ```properties
   spring.datasource.password=<your MySQL password>
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

4. Open Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## How to Run Tests

```bash
./mvnw test
```

## Deployment Notes

- Hosted on **Railway** with a multi-stage **Dockerfile**.
- Environment variables on Railway:
  - `DB_URL` — JDBC connection string to Railway MySQL
  - `DB_USER` — database username
  - `DB_PASSWORD` — database password
- `server.forward-headers-strategy=framework` is set so the app trusts Railway's reverse proxy headers (required for correct HTTPS URLs in Swagger).
