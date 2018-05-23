package com.basaki.blockchain.exception;

public class InvalidCryptoException extends RuntimeException {

    public InvalidCryptoException(String message) {
        super(message);
    }

    public InvalidCryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
