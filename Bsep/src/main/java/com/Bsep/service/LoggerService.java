package com.Bsep.service;

public interface LoggerService {
    void loginSuccess(String email);
    void loginFailed(String email);
    void certificateCreated(String issuerEmail, String subjectEmail);
    void certificateCreatingFailed(String issuerEmail, String subjectEmail);
    void allCertificates(String userEmail);
    void userCertificates(String userEmail);
    void certificateDownloadSuccess(String userEmail);
    void certificateDownloadFailed(String userEmail);
    void certificateRevokingSuccess(String userEmail);
    void checkIfCertificateIsRevoked(String userEmail);
    void checkIfCertificateIsValid(String userEmail);
    void getAllUsers(String userEmail);
}
