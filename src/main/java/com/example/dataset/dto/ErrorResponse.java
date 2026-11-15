package com.example.dataset.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard error response")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type", example = "Bad Request")
    private String error;

    @Schema(description = "Error detail", example = "Record must have an 'id' field")
    private String message;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
}
