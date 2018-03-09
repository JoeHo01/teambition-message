package com.d1m.tbmessage.server.database.entity;

public class ProjectDO {

	private String id;

	private String name;

	private String description;

	private String projectTag;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProjectTag() {
		return projectTag;
	}

	public void setProjectTag(String projectTag) {
		this.projectTag = projectTag;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
}
