package com.Bsep.service;

public interface LoggerService {
    void loginSuccess(String username, String ip);
    void loginFailed(String username, String ip);
    void certificateCreated(String issuerUsername, String subjectUsername);
    void certificateCreatingFailed(String issuerUsername, String subjectUsername);
    void passwordChanged(String username);
    void passwordChangingFailed(String message, String username);
    void allCertificates(String username);
    void userCertificates(String username);
    void certificateDownloadSuccess(String username, Long certId);
    void certificateDownloadFailed(String username, Long certId);
    void certificateRevokingSuccess(String username, String certSerial);
    void checkIfCertificateIsRevoked(String username, String certSerial);
    void checkIfCertificateIsValid(String username, String certSerial);
    void unauthorizedAccess(String method, String path, String ip);
    void getAllUsers(String username);
}
