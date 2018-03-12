package com.d1m.tbmessage.server.teambition.config;

import com.d1m.tbmessage.common.annotation.Name;

public class AppSecretInfo {

	private static AppSecretInfo instance;

	/** x_api_key for teambition api */
	@Name("x_api_key")
	private String apiKey;

	/** access_token for teambition api */
	@Name("access_token")
	private String accessToken;

	/** code for teambition access_token api */
	@Name("code")
	private String code;

	/** teambition app client_id */
	@Name("client_id")
	private String clientId;

	/** teambition app client_secret */
	@Name("clint_secret")
	private String clientSecret;

	/** teambition organization_id */
	private String organizationId;

	public static AppSecretInfo getInstance() {
		if (instance == null) {
			instance = new AppSecretInfo();
		}
		return instance;
	}

	private AppSecretInfo(){

	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
}
