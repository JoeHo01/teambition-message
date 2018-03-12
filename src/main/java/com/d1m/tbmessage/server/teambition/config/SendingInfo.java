package com.d1m.tbmessage.server.teambition.config;

import java.util.HashMap;
import java.util.Map;

public class SendingInfo {

	private static SendingInfo instance;

	private Map<String, String> mapper;

	private Map<String, String> project;

	private SendingInfo() {
		mapper = new HashMap<>();
		project = new HashMap<>();
	}

	public static SendingInfo getInstance() {
		if (instance == null) {
			instance = new SendingInfo();
		}
		return instance;
	}

	public void setProject(String projectName, String projectId) {
		project.put(projectName, projectId);
	}

	public void setId(String groupId, String projectName) {
		mapper.put(groupId, project.get(projectName));
	}

	public String getProjectId(String groupId){
		return mapper.get(groupId);
	}
}
