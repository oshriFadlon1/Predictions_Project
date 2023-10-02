package com.example.server.engineDtos;

import org.springframework.http.HttpStatus;

public class DtoResponseToController {
    private String message;
    private HttpStatus httpStatus;
    private boolean isSucceed;

    public DtoResponseToController(String message, HttpStatus httpStatus, boolean isSucceed) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.isSucceed = isSucceed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public boolean isSucceed() {
        return isSucceed;
    }

    public void setSucceed(boolean succeed) {
        isSucceed = succeed;
    }

    @Override
    public String toString() {
        return "DtoResponseToController{" +
                "message='" + message + '\'' +
                ", httpStatus=" + httpStatus +
                ", isSucceed=" + isSucceed +
                '}';
    }
}
