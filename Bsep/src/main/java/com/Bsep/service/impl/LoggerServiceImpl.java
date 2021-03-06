package com.bsep.service.impl;

import com.bsep.service.LoggerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerServiceImpl implements LoggerService {

    private final Logger logger;

    public LoggerServiceImpl(Class<?> parentClass) {this.logger = LogManager.getLogger(parentClass); }

    @Override
    public void loginSuccess(String username, String ip) {
        logger.info("Login successful. Username: {} From: {}", username, ip);
    }

    @Override
    public void loginFailed(String username, String ip) {
        logger.warn("Login failed. Username: {} From: {}", username, ip);
    }

    @Override
    public void certificateCreated(String issuerUsername, String subjectUsername) {
        logger.info("Certificate created. Issuer username: {} Subject username: {} ", issuerUsername, subjectUsername);
    }

    @Override
    public void certificateCreatingFailed(String issuerUsername, String subjectUsername) {
        logger.warn("Certificate creation failed. Issuer username: {} Subject username: {}", issuerUsername, subjectUsername);
    }

    @Override
    public void allCertificates(String username) {
        logger.info("All certificates pulled from keystore. Username: {}", username);
    }

    @Override
    public void userCertificates(String username) {
        logger.info("User certificates pulled from keystore. Username: {}", username);
    }

    @Override
    public void certificateDownloadSuccess(String username, Long certId) {
        logger.info("Certificate downloaded successfully. Username: {} Certificate id: {}", username, certId);
    }

    @Override
    public void certificateDownloadFailed(String username, Long certId) {
        logger.warn("Certificate download failed. Username: {} Certificate id: {}", username, certId);
    }

    @Override
    public void certificateRevokingSuccess(String username, String certSerial) {
        logger.info("Certificate revoked successfully. Username: {} Certificate serial number: {}", username, certSerial);
    }

    @Override
    public void checkIfCertificateIsRevoked(String username, String certSerial) {
        logger.info("Certificate status checked successfully. Username: {} Certificate serial number: {}", username, certSerial);
    }

    @Override
    public void checkIfCertificateIsValid(String username, String certSerial) {
        logger.info("Certificate validity checked successfully: Username: {} Certificate serial number: {}", username, certSerial);
    }

    @Override
    public void getAllUsers(String username) {
        logger.info("All users successfully gotten. Username: {}", username);
    }

    @Override
    public void unauthorizedAccess(String method, String path, String ip) {
        logger.warn("Unauthorized access to {}: {}. From: {}", method, path, ip);
    }
    
    @Override
    public void passwordChanged(String username) {
        logger.info("Password successfully changed. Username: {}", username);
    }
    
    @Override
    public void passwordChangingFailed(String message, String username) {
        logger.warn("Password changing failed: {}. Email: {}", message, username);
    }
    
    @Override
    public void get2FAStatus(String username) {
        logger.info("Gotten two-factor authentication status. Username: {}", username);
    }
    
    @Override
    public void change2FAStatus(String username, Boolean isEnabled) {
        logger.warn("Two-factor authentication status changed on: {}. Username: {}",isEnabled, username);
    }
    
    @Override
    public void change2FAStatusFailed(String username) {
        logger.info("Two-factor authentication status change failure. Username: {}", username);
    }
    
    @Override
    public void loginFailedCodeNotMatching(String username, String ip) {
        logger.warn("Login failed, invalid code. Username: {} From: {}", username, ip);
    }
    
    @Override
    public void login2FACheck(String username, String ip) {
        logger.info("Two-factor authentication status check done. Username: {} From: {}", username, ip);
    }
    
    @Override
    public void login2FACheckFailed(String username, String ip) {
        logger.warn("Login failed. Username: {} From: {}", username, ip);
    }
    
    @Override
    public void providerError() {
        logger.error("Can't find provider.");
    }
    
    @Override
    public void errorLog(Exception e) {
        logger.error(e);
    }

}
