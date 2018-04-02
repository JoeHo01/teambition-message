package com.d1m.tbmessage.server.teambition.config;

public enum TeambitionURL {
	BASE("https://www.teambition.com","Teambition URL"),
	MESSAGE_SEND(BASE.url + "/appstore/api/developer/chats/message", "Send message"),
	PROJECT_TAGS_GET(BASE.url + "/api/organizations/{}/projecttags", "Get ProjectTags bellow to the Company"),
	PROJECTS_GET(BASE.url + "/api/organizations/{}/projects", "Get Projects bellow to the Company"),
	ACCESS_TOKEN_GET(" https://account.teambition.com/api/oauth2/access_token", "Get APP access token")
	;

	private String url;

	private String description;

	TeambitionURL(String url, String description) {
		this.url = url;
		this.description = description;
	}

	public String url() {
		return url;
	}

	public String description() {
		return description;
	}
}
