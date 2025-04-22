package com.sintergica.apiv2.exceptions.email;

public class EmailNotFound extends RuntimeException {
    public EmailNotFound(String message) {
        super(message);
    }
}
