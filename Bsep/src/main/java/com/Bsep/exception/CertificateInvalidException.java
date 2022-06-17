package com.bsep.exception;

public class CertificateInvalidException extends RuntimeException{
	public CertificateInvalidException() {
        super("Certificate is invalid.");
    }
}
