package com.sintergica.apiv2.exceptions.password;

public class TokenPasswordHasBeenSended extends RuntimeException {
    public TokenPasswordHasBeenSended(String message) {
        super(message);
    }
}
