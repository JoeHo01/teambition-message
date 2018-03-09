package com.d1m.tbmessage.server.teambition.entity;

import org.apache.commons.collections.CollectionUtils;

import java.util.Iterator;
import java.util.List;

public class SendMessageDTO {

	private String organizationId;

	private List<String> users;

	private List<String> groups;

	private List<String> projects;

	private String messageType;

	private String text;

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public List<String> getProjects() {
		return projects;
	}

	public void setProjects(List<String> projects) {
		this.projects = projects;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String toString() {
		return "{"
				+ "\"_organizationId\":\"" + organizationId + "\","
				+ "\"users\":[\""+ getString(users) +"\"],"
				+ "\"groups\":[\""+ getString(groups) +"\"],"
				+ "\"projects\":[\""+ getString(projects) +"\"],"
				+ "\"messageType\":\""+ messageType +"\","
				+ "\"text\":\"" + text
				+ "\"}";
	}

	private String getString(List<String> list){
		if (CollectionUtils.isEmpty(list)) return "";
		Iterator<String> iterator = list.iterator();
		StringBuilder result = new StringBuilder();
		while (iterator.hasNext()){
			result.append("\"").append(iterator.hasNext()).append("\"");
			if (iterator.hasNext()) result.append(",");
		}
		return result.toString();
	}
}
