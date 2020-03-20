package ru.web.webapp.exceptions;

public class UserServiceException extends RuntimeException {

    private static final long serialVersionUID = 4553263929395228383L;

    public UserServiceException(String message) {
        super(message);
    }
}
