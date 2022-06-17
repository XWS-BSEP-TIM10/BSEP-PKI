package com.bsep.dto;

public class CertificateRevocationStatusDTO {
	private Boolean status;
	private String revocationReason;
	
	public CertificateRevocationStatusDTO() {
		super();
	}

	
	public CertificateRevocationStatusDTO(Boolean status, String revocationReason) {
		super();
		this.status = status;
		this.revocationReason = revocationReason;
	}

	
	
	public Boolean getStatus() {
		return status;
	}


	public void setStatus(Boolean status) {
		this.status = status;
	}


	public String getRevocationReason() {
		return revocationReason;
	}

	public void setRevocationReason(String revocationReason) {
		this.revocationReason = revocationReason;
	}
	
	
}
