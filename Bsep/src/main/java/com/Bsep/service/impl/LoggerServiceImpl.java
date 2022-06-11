package com.Bsep.service.impl;

import com.Bsep.service.LoggerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerServiceImpl implements LoggerService {

    private final Logger logger;

    public LoggerServiceImpl(Class<?> parentClass) {this.logger = LogManager.getLogger(parentClass); }

    @Override
    public void loginSuccess(String email) {
        logger.info("Login successful. Email: " + email);
    }

    @Override
    public void loginFailed(String email) {
        logger.warn("Login failed. Email: " + email);
    }

    @Override
    public void certificateCreated(String issuerEmail, String subjectEmail) {
        logger.info("Certificate created. Issuer: " + issuerEmail +  " Subject: " + subjectEmail);
    }

    @Override
    public void certificateCreatingFailed(String issuerEmail, String subjectEmail) {
        logger.warn("Certificate creation failed. Issuer: " + issuerEmail +  " Subject: " + subjectEmail);
    }

    @Override
    public void allCertificates(String userEmail) {
        logger.info("All certificates pulled from keystore. Email: " + userEmail);
    }

    @Override
    public void userCertificates(String userEmail) {
        logger.info("User certificates pulled from keystore. Email: " + userEmail);
    }

    @Override
    public void certificateDownloadSuccess(String userEmail) {
        logger.info("Certificate downloaded successfully. Email: " + userEmail);
    }

    @Override
    public void certificateDownloadFailed(String userEmail) {
        logger.warn("Certificate download failed. Email: " + userEmail);
    }

    @Override
    public void certificateRevokingSuccess(String userEmail) {
        logger.info("Certificate revoked successfully. Email: " + userEmail);
    }

    @Override
    public void checkIfCertificateIsRevoked(String userEmail) {
        logger.info("User with email: " + userEmail + " checked if certificate is revoked");
    }

    @Override
    public void checkIfCertificateIsValid(String userEmail) {
        logger.info("User with email: " + userEmail + " checked if certificate is valid");
    }

    @Override
    public void getAllUsers(String userEmail) {
        logger.info("User with email: " + userEmail + " has gotten all users");
    }

}
