package org.example.exception;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LandmarkNotFoundException extends RuntimeException {
    public LandmarkNotFoundException() {
    }

    public LandmarkNotFoundException(String message) {
        super(message);
    }

    public LandmarkNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LandmarkNotFoundException(Throwable cause) {
        super(cause);
    }

    public LandmarkNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
