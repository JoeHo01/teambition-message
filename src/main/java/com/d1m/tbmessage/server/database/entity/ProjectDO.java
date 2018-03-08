package com.d1m.tbmessage.server.database.entity;

public class ProjectDO {

	private String id;

	private String name;

	private String description;

	private String project_tag;

	private String organization_id;

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

	public String getProject_tag() {
		return project_tag;
	}

	public void setProject_tag(String project_tag) {
		this.project_tag = project_tag;
	}

	public String getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}
}
