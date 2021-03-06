package com.bsep.dto;

import com.bsep.model.CertificateStatus;
import com.bsep.model.CertificateType;

public class CertificateDto {

    private Long id;
    private String serialNumber;
    private String username;
    private String organization;
    private String organizationalUnitName;
    private String countryCode;
    private String issuerCommonName;
    private String startDate;
    private String endDate;
    private CertificateStatus status;
    private CertificateType certificateType;

    public CertificateDto() {
    }

    public CertificateDto(Long id, String serialNumber, String username, String organization, String organizationalUnitName, String countryCode, String issuerCommonName, String startDate, String endDate, CertificateStatus status, CertificateType certificateType) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.username = username;
        this.organization = organization;
        this.organizationalUnitName = organizationalUnitName;
        this.countryCode = countryCode;
        this.issuerCommonName = issuerCommonName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.certificateType = certificateType;
    }

    public Long getId() {return id;}

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getOrganization() {
        return organization;
    }

    public String getOrganizationalUnitName() {
        return organizationalUnitName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getIssuerCommonName() {
        return issuerCommonName;
    }

    public String getEndDate() {
        return endDate;
    }

    public CertificateStatus getStatus() {
        return status;
    }

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public String getStartDate() {return startDate;}

}
