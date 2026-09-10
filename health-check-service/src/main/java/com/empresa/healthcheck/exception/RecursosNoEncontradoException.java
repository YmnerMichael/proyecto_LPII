package com.empresa.healthcheck.exception;

public class RecursosNoEncontradoException extends RuntimeException {
    public RecursosNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
