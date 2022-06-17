package com.bsep.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.bsep.model.CertificatePurposeType;
import com.bsep.model.CertificateType;

public class NewCertificateDto {
	
	@NotBlank(message = "Subject is mandatory")
    private Long subjectUID;
	@NotBlank(message = "Organization is mandatory")
    private String organization;
	@NotBlank(message = "Organization unit name is mandatory")
    private String organizationalUnitName;
	@NotBlank(message = "Organization email is mandatory")
    private String organizationEmail;
	@NotBlank(message = "Country code is mandatory")
    private String countryCode;
    private String issuerCertificateSerialNumber;
    @NotBlank(message = "End date is mandatory")
    private String endDate;
    @NotBlank(message = "Certificate type is mandatory")
    private CertificateType certificateType;
    private List<String> keyUsages;
    private List<String> extendedKeyUsages;

    public NewCertificateDto() {
    }

	public NewCertificateDto(Long subjectUID, String organization, String organizationalUnitName,
			String organizationEmail, String countryCode, String issuerCertificateSerialNumber, String endDate,
			CertificateType certificateType, List<String> keyUsages, List<String> extendedKeyUsages) {
		super();
		this.subjectUID = subjectUID;
		this.organization = organization;
		this.organizationalUnitName = organizationalUnitName;
		this.organizationEmail = organizationEmail;
		this.countryCode = countryCode;
		this.issuerCertificateSerialNumber = issuerCertificateSerialNumber;
		this.endDate = endDate;
		this.certificateType = certificateType;
		this.keyUsages = keyUsages;
		this.extendedKeyUsages = extendedKeyUsages;
	}





	public Long getSubjectUID() {
        return subjectUID;
    }

    public String getOrganization() {
        return organization;
    }

    public String getOrganizationalUnitName() {
        return organizationalUnitName;
    }

    public String getOrganizationEmail() {
        return organizationEmail;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getIssuerCertificateSerialNumber() {
        return issuerCertificateSerialNumber;
    }

    public String getEndDate() {
        return endDate;
    }

    public CertificateType getCertificateType() {
        return certificateType;
    }

	public List<String> getKeyUsages() {
		return keyUsages;
	}

	public List<String> getExtendedKeyUsages() {
		return extendedKeyUsages;
	}
		
}
