package com.d1m.tbmessage.server.database.entity;


public class MsgDO {
	private long id;
	private String groupId;
	private String sender;
	private long createdTime;
	private String content;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
