package com.d1m.tbmessage.tbconnection.constant;

public enum TeambitionURL {
	BASE("https://www.teambition.com","Teambition URL"),
	MESSAGE_SEND(BASE.url + "/appstore/api/developer/chats/message", "Send message"),
	PROJECT_TAGS_GET(BASE.url + "/api/organizations/{}/projecttags", "Get ProjectTags bellowed to the Company"),
	;

	private String url;

	private String description;

	TeambitionURL(String url, String description) {
		this.url = url;
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
