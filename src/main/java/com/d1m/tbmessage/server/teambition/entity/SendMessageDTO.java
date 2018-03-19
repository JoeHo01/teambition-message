package com.d1m.tbmessage.server.teambition.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SendMessageDTO {

	private String organizationId;

	private List<String> projects;

	private List<String> users;

	private List<String> groups;

	private String messageType;

	private String text;

	public SendMessageDTO() {
		this.users = new ArrayList<>();
		this.groups = new ArrayList<>();
		this.projects = new ArrayList<>();
	}

	public void addUser(String user) {
		this.users.add(user);
	}

	public void addGroup(String group){
		this.groups.add(group);
	}

	public void addProject(String project) {
		this.projects.add(project);
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public List<String> getProjects() {
		return projects;
	}

	public void setProjects(List<String> projects) {
		this.projects = projects;
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
		StringBuilder result = new StringBuilder();
		result.append("{");
		if (!projects.isEmpty()) {
			result.append("\"projects\":[").append(getString(projects)).append("],");
		}
		if (!users.isEmpty()) {
			result.append("\"users\":[").append(getString(users)).append("],");
		}
		if (!groups.isEmpty()) {
			result.append("\"groups\":[").append(getString(groups)).append("],");
		}
		result.append("\"_organizationId\":\"").append(organizationId).append("\",");
		result.append("\"_messageType\":\"").append(messageType).append("\",");
		result.append("\"text\":\"").append(text);
		result.append("\"}");
		return result.toString();
	}

	private String getString(List<String> list){
		if (!list.isEmpty()){
			Iterator<String> iterator = list.iterator();
			StringBuilder result = new StringBuilder();
			while (iterator.hasNext()){
				result.append("\"").append(iterator.next()).append("\"");
				if (iterator.hasNext()) result.append(",");
			}
			return result.toString();
		}else {
			return "";
		}
	}
}
