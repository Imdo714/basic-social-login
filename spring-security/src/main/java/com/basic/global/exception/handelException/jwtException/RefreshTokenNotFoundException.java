package com.basic.global.exception.handelException.jwtException;

public class RefreshTokenNotFoundException extends CustomJwtException {

    public RefreshTokenNotFoundException(String message) {
        super(message);
    }
}
