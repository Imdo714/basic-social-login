package com.basic.global.exception.handelException.jwtException;

public class JwtExpiredException extends CustomJwtException {

    public JwtExpiredException(String message) {
        super(message);
    }
}
