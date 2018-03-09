package com.d1m.tbmessage.server.teambition.config;

public class ConnectionInfo {

	public static ConnectionInfo instance;

	/** x_api_key for teambition api */
	private String apiKey;

	/** access_token for teambition api */
	private String accessToken;

	/** code for teambition access_token api */
	private String code;

	/** teambition app client_id */
	private String clientId;

	/** teambition app client_secret */
	private String clientSecret;

	public static ConnectionInfo getInstance() {
		if (instance == null) {
			instance = new ConnectionInfo();
		}
		return instance;
	}

	private ConnectionInfo(){

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
}
