package com.Bsep.exception;

public class WrongPasswordException extends Exception {
    public WrongPasswordException() {
        super("Wrong password");
    }
}
