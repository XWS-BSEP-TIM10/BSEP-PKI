package com.bsep.dto;

public class TwoFactorAuthenticationDTO {
	
	private Boolean isEnabled;
	private String secret;
	
	public TwoFactorAuthenticationDTO() {
		super();
	}

	public TwoFactorAuthenticationDTO(Boolean isEnabled, String secret) {
		super();
		this.isEnabled = isEnabled;
		this.secret = secret;
	}

	public Boolean getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	
	

}
