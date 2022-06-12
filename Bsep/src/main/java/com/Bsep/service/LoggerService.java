package com.Bsep.service;

public interface LoggerService {
    void loginSuccess(String username);
    void loginFailed(String username);
    void certificateCreated(String issuerUsername, String subjectUsername);
    void certificateCreatingFailed(String issuerUsername, String subjectUsername);
    void allCertificates(String username);
    void userCertificates(String username);
    void certificateDownloadSuccess(String username, Long certId);
    void certificateDownloadFailed(String username, Long certId);
    void certificateRevokingSuccess(String username, String certSerial);
    void checkIfCertificateIsRevoked(String username, String certSerial);
    void checkIfCertificateIsValid(String username, String certSerial);
    void getAllUsers(String username);
}
