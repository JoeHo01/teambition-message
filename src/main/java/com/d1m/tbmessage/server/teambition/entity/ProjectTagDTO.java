package com.d1m.tbmessage.server.teambition.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ProjectTagDTO {

	@JsonProperty("_id")
	private String id;

	private String name;

	private List<ProjectDTO> projects;

	private String organizationId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ProjectDTO> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectDTO> projects) {
		this.projects = projects;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
}
