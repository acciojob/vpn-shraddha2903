package com.driver.Exceptions;

public class UnableToConnect extends RuntimeException{
    public UnableToConnect(String message) {
        super(message);
    }
}
