package com.Bsep.dto;

public class RevokeCertificateDTO {
	private String reason;

	public RevokeCertificateDTO() {
		super();
	}

	public RevokeCertificateDTO(String reason) {
		super();
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}
	
	
}
