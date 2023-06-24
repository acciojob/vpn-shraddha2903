package com.driver.Exceptions;

public class UserAlreadyConnected extends  Exception{
    public UserAlreadyConnected(String message) {
        super(message);
    }
}
